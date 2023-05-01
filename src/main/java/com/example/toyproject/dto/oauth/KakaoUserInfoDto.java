package com.example.toyproject.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KakaoUserInfoDto {
    private Long id;
    private String connected_at;
    private KakaoProperties properties;
    private KakaoAccount kakao_account;

    @Data
    public static class KakaoProperties{
        private String nickname;
        private String profile_image;
        private String thumbnail_image;

    }

    @Data
    public static class KakaoAccount{
        private String email;
        private Boolean profile_nickname_needs_agreement;
        private Boolean profile_image_needs_agreement;
        private KakaoProfile profile;
        private Boolean name_needs_agreement;
        private Boolean email_needs_agreement;
        private Boolean has_email;
        private Boolean is_email_valid;
        private Boolean is_email_verified;

        @Data
        public static class KakaoProfile{
            private String nickname;
            private String thumbnail_image_url;
            private String profile_image_url;
            private Boolean is_default_image;
        }
    }

}
