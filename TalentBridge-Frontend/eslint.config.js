import fs from 'node:fs'
import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import tseslint from 'typescript-eslint'

/*
 * Clean Architecture import rules (the frontend counterpart of the backend ArchitectureTest).
 *
 *   presentation -> application -> domain <- infrastructure
 *
 * - domain:         pure TypeScript. No framework, no HTTP, no other layer.
 * - application:    use cases and the ports they need. Knows domain only.
 * - infrastructure: implements ports (HTTP, browser storage). Never imports presentation.
 * - presentation:   React. Gets use cases from the composition root (@/app/container), never infrastructure.
 * - features are isolated: another feature may only be reached through its public API
 *   (@/features/<name>) or its domain types.
 */

const featureNames = fs
  .readdirSync(new URL('./src/features', import.meta.url), { withFileTypes: true })
  .filter((entry) => entry.isDirectory())
  .map((entry) => entry.name)

const UI_LIBS = [
  'react', 'react/*', 'react-dom', 'react-dom/*', 'react-router-dom', 'react-redux', '@reduxjs/*',
  'redux-persist', 'redux-persist/*', 'sonner', 'lucide-react', '@radix-ui/*', 'next-themes',
  'react-quill-new', '@react-pdf-viewer/*', 'class-variance-authority', 'clsx', 'tailwind-merge',
]
const HTTP_LIBS = ['axios']

const forbid = (group, message) => ({ group, message })

/** Everything that is not domain code, for the layers that must stay pure. */
const OUTER_LAYERS = [
  forbid(['**/application/**', '**/infrastructure/**', '**/presentation/**'], 'Inner layers must not depend on outer ones.'),
  forbid(['@/app/**'], 'Only presentation may use the composition root, and only @/app/container.'),
]

/** Other features may only be used through their public API or their domain types. */
const otherFeatures = (feature, { allowPublicApi }) =>
  featureNames
    .filter((name) => name !== feature)
    .flatMap((name) => [
      forbid(
        [`@/features/${name}/application/**`, `@/features/${name}/infrastructure/**`, `@/features/${name}/presentation/**`],
        `Use the public API of the ${name} feature (@/features/${name}) or its domain types.`,
      ),
      ...(allowPublicApi
        ? []
        : [forbid([`@/features/${name}`, `@/features/${name}/index`], `Only presentation may use the public API of the ${name} feature.`)]),
    ])

const layer = (name, files, patterns, paths = []) => ({
  name,
  files,
  rules: { 'no-restricted-imports': ['error', { paths, patterns }] },
})

const libPatterns = (names, message) => [forbid(names, message)]

const sharedLayers = [
  layer('shared/domain', ['src/shared/domain/**/*.{ts,tsx}'], [
    ...OUTER_LAYERS,
    forbid(['@/features/**'], 'The shared kernel must not depend on a feature.'),
    ...libPatterns([...UI_LIBS, ...HTTP_LIBS], 'The domain is framework free.'),
  ]),
  layer('shared/infrastructure', ['src/shared/infrastructure/**/*.{ts,tsx}'], [
    forbid(['**/application/**', '**/presentation/**'], 'Infrastructure must not depend on presentation or application.'),
    forbid(['@/features/**', '@/app/**'], 'The shared kernel must not depend on a feature or the app shell.'),
    ...libPatterns(UI_LIBS, 'Infrastructure has no UI.'),
  ]),
  layer('shared/presentation', ['src/shared/presentation/**/*.{ts,tsx}'], [
    forbid(['**/infrastructure/**', '**/application/**'], 'The shared UI kit must not depend on infrastructure.'),
    forbid(['@/features/**', '@/app/**'], 'The shared UI kit must not depend on a feature or the app shell.'),
    ...libPatterns(HTTP_LIBS, 'No HTTP in presentation.'),
  ]),
]

const featureLayers = featureNames.flatMap((feature) => {
  const dir = `src/features/${feature}`
  return [
    layer(`${feature}/domain`, [`${dir}/domain/**/*.{ts,tsx}`], [
      ...OUTER_LAYERS,
      forbid(['@/shared/infrastructure/**', '@/shared/presentation/**'], 'The domain only knows the shared domain.'),
      ...otherFeatures(feature, { allowPublicApi: false }),
      ...libPatterns([...UI_LIBS, ...HTTP_LIBS], 'The domain is framework free.'),
    ]),
    layer(`${feature}/application`, [`${dir}/application/**/*.{ts,tsx}`], [
      forbid(['**/infrastructure/**', '**/presentation/**'], 'Use cases depend on ports, not on adapters or UI.'),
      forbid(['@/app/**'], 'Only presentation may use the composition root, and only @/app/container.'),
      forbid(['@/shared/infrastructure/**', '@/shared/presentation/**'], 'Use cases only know the shared domain.'),
      ...otherFeatures(feature, { allowPublicApi: false }),
      ...libPatterns([...UI_LIBS, ...HTTP_LIBS], 'Use cases are framework free.'),
    ]),
    layer(`${feature}/infrastructure`, [`${dir}/infrastructure/**/*.{ts,tsx}`], [
      forbid(['**/presentation/**'], 'Adapters must not depend on presentation.'),
      forbid(['@/app/**', '@/shared/presentation/**'], 'Adapters must not depend on the app shell or the UI kit.'),
      ...otherFeatures(feature, { allowPublicApi: false }),
      ...libPatterns(UI_LIBS, 'Adapters have no UI.'),
    ]),
    layer(`${feature}/presentation`, [`${dir}/presentation/**/*.{ts,tsx}`], [
      forbid(['**/infrastructure/**'], 'Presentation gets its use cases from @/app/container, never from infrastructure.'),
      forbid(['@/app/**', '!@/app/container'], 'Presentation may only use the composition root (@/app/container).'),
      ...otherFeatures(feature, { allowPublicApi: true }),
      ...libPatterns(HTTP_LIBS, 'No HTTP in presentation: call a use case.'),
    ]),
  ]
})

export default tseslint.config(
  { ignores: ['dist'] },
  {
    extends: [js.configs.recommended, ...tseslint.configs.recommended],
    files: ['**/*.{ts,tsx}'],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
    },
    plugins: {
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
    },
    rules: {
      ...reactHooks.configs.recommended.rules,
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],
    },
  },
  {
    // Chai assertions such as `expect(x).to.be.true` are expressions by design.
    files: ['cypress/**/*.ts'],
    rules: { '@typescript-eslint/no-unused-expressions': 'off' },
  },
  ...sharedLayers,
  ...featureLayers,
)
