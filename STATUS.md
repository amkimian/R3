# Status

Migration reviewed 2026-09-30. Lifecycle: undecided.

Canonical location: `D:\Development\Platforms\R3`.
Original location retained intact: `C:\Users\ukmoo\development\R3`.

Windows interrupted the cross-volume move. Recovery restored all 136 original files at the original location and verified the complete canonical copy against pre-move SHA256 hashes. No historical copy was deleted. The original copy is a safety copy; make future changes at the canonical location.

Existing remote https://github.com/amkimian/R3 was verified PUBLIC under the authenticated user's account. Publishing is paused: this workflow requires private repositories. No push or visibility change was performed. Choose a separate private repository or private fork while preserving this project's history.

Original HEAD: 31d526701d8573c5900914f87c9af143a558c406. Working tree was clean before migration. README and this status are migration documentation only.

Offline Maven validation could not resolve uncached maven-surefire-plugin 3.2.2; no tests ran. Existing dynamic JUnit RELEASE dependency remains unchanged. Runtime behavior is unverified.
