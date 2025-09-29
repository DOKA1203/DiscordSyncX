# DiscordSyncX


[![시연 동영상](http://img.youtube.com/vi/9q4E2JzCRJk/0.jpg)](https://www.youtube.com/watch?v=9q4E2JzCRJk)


[![GitHub Actions Build Status](https://img.shields.io/github/actions/workflow/status/doka1203/DiscordSyncX/build.yml?branch=main&style=for-the-badge)](https://github.com/doka1203/DiscordSyncX/actions)
[![Latest Release](https://img.shields.io/github/v/release/doka1203/DiscordSyncX?style=for-the-badge&color=blue)](https://github.com/doka1203/DiscordSyncX/releases)
[![License](https://img.shields.io/github/license/doka1203/DiscordSyncX?style=for-the-badge&color=lightgrey)](https://github.com/doka1203/DiscordSyncX/blob/main/LICENSE)

> A powerful yet simple plugin to seamlessly synchronize your PaperMC server chat and events with a Discord channel. Built with modern Kotlin Coroutines for high performance.

**DiscordSyncX**는 마인크래프트 서버와 디스코드 커뮤니티를 하나로 잇는 가장 확실한 방법입니다. 사용자를 인증하고, 서버 내의 대화와 주요 이벤트들을 지정된 디스코드 채널로 실시간 전송하고, 반대로 디스코드 채널의 대화를 서버로 가져올 수 있습니다.

## ✨ Features(구현됨)
* **✍️ (중요) 유저 인증**: 인증된 플레이어만 서버에 접속할 수 있도록 허용   <br/><br/>

## ✨ Features(구현 예정)
* **🔄 양방향 채팅**: 마인크래프트 채팅 ↔ 디스코드 채널 간의 완벽한 실시간 채팅 동기화
* **👋 접속 알림**: 플레이어의 서버 접속/퇴장 상태를 디스코드 채널에 알림
* **⚫ 서버 상태 알림**: 서버 시작 및 종료 상태를 디스코드에 전송하여 서버 상태를 쉽게 확인
* **⚡️ 비동기 처리**: 코틀린 코루틴 기반의 비동기 네트워크 처리로 서버 성능 저하 최소화
* **🔧 간단한 설정**: 직관적인 설정 파일로 누구나 쉽게 설정 가능

## 🚀 Getting Started

### Prerequisites (tested)

* PaperMC 1.21+ ( not spigot )
* Java 21+

### Installation & Setup

1.  **플러그인 다운로드**
    * [GitHub Releases](https://github.com/doka1203/DiscordSyncX/releases) 페이지에서 최신 버전의 `.jar` 파일을 다운로드합니다.

2.  **플러그인 설치**
    * 다운로드한 `.jar` 파일을 PaperMC 서버의 `plugins/` 폴더에 넣습니다.

3.  **서버 최초 실행**
    * 서버를 한 번 실행하여 `plugins/DiscordSyncX/config.yml` 파일이 생성되도록 합니다.

4.  **디스코드 봇 생성 및 설정**
    * [Discord 개발자 포털](https://discord.com/developers/applications)로 이동하여 `New Application`을 클릭해 새 봇을 생성합니다.
    * `Bot` 탭에서 **봇 토큰(Token)**을 복사합니다. (`Reset Token` 클릭)
    * `Privileged Gateway Intents` 섹션에서 `SERVER MEMBERS INTENT`와 `MESSAGE CONTENT INTENT`를 활성화합니다.
    * `OAuth2` > `URL Generator` 탭에서 `bot` 스코프를 선택하고, `Bot Permissions`에서 `Send Messages`, `Read Message History`, `View Channel` 권한을 부여한 후, 생성된 URL을 통해 당신의 디스코드 서버에 봇을 추가합니다.

5.  **플러그인 설정**
    * `plugins/DiscordSyncX/config.yml` 파일을 열어 복사해 둔 **봇 토큰**과 동기화할 **채널 ID**를 입력합니다.
    > 채널 ID는 디스코드 설정 > 고급 > 개발자 모드를 활성화한 후, 원하는 채널을 우클릭하여 'ID 복사하기'로 얻을 수 있습니다.

6.  **서버 재시작**
    * 서버를 재시작하거나, `/dsx reload` 명령어를 입력하여 설정을 적용합니다. 이제 동기화가 시작됩니다!

## ⚙️ Configuration

`config.yml` 파일을 통해 플러그인의 대부분의 기능을 제어할 수 있습니다.

```yaml
# Discord Bot Settings
# 디스코드 봇 설정
bot-token: "YOUR_BOT_TOKEN_HERE"
guild-id: "YOUR_DISCORD_GUILD_ID_HERE"

discord:
    clientId:
    clientSecret:
    redirectUri:

# Database Settings
# 데이타베이스 설정 (MariaDB만 지원)
database:
  host: "localhost"
  port: 3306
  database: "discordsyncx"
  username: "root"
  password: ""
