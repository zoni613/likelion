package hello.backendproject.security.core;

// 회원가입 시 사용자의 권한을 정의
// 일반유저인지 관리자인지 구분
public enum Role {

    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN");

    private String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
