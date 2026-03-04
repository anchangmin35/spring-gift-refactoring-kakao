Feature: 회원가입 및 로그인

  Scenario: 회원가입에 성공하면 토큰이 발급된다
    When "member@test.com" 이메일과 "password" 비밀번호로 회원가입하면
    Then 회원가입에 성공한다
    And 응답에 토큰이 포함되어 있다

  Scenario: 이미 등록된 이메일로 회원가입하면 실패한다
    Given "member@test.com" 이메일과 "password" 비밀번호로 회원가입하면
    When "member@test.com" 이메일과 "other" 비밀번호로 회원가입하면
    Then 회원가입이 거부된다

  Scenario: 올바른 이메일과 비밀번호로 로그인하면 토큰이 발급된다
    Given "member@test.com" 이메일과 "password" 비밀번호로 회원가입하면
    When "member@test.com" 이메일과 "password" 비밀번호로 로그인하면
    Then 로그인에 성공한다
    And 응답에 토큰이 포함되어 있다

  Scenario: 잘못된 비밀번호로 로그인하면 실패한다
    Given "member@test.com" 이메일과 "password" 비밀번호로 회원가입하면
    When "member@test.com" 이메일과 "wrong" 비밀번호로 로그인하면
    Then 로그인이 거부된다

  Scenario: 존재하지 않는 이메일로 로그인하면 실패한다
    When "unknown@test.com" 이메일과 "password" 비밀번호로 로그인하면
    Then 로그인이 거부된다
