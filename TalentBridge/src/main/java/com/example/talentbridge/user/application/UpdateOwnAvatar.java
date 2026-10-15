package com.example.talentbridge.user.application;

import com.example.talentbridge.shared.domain.exception.NotFoundException;
import com.example.talentbridge.shared.application.CurrentUser;
import com.example.talentbridge.shared.application.FileStorage;
import com.example.talentbridge.shared.application.FileUpload;
import com.example.talentbridge.user.domain.User;
import com.example.talentbridge.user.domain.UserDetails;
import com.example.talentbridge.user.domain.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateOwnAvatar {

    private final UserRepository users;
    private final FileStorage storage;
    private final CurrentUser currentUser;

    public void execute(FileUpload avatar) {
        User user = users.findByEmail(currentUser.email())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));

        if (avatar != null && !avatar.isEmpty())
            user.changeAvatar(storage.uploadFile(avatar, "avatar", user.getId().toString(), true));

        users.save(user);
    }
}
