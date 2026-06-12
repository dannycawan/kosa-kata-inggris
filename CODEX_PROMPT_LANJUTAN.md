# Prompt Lanjutan - UI/UX Polish

You are continuing the Android Kotlin project "3000 Kosakata Inggris".

The build foundation is expected to be stable and GitHub Actions should already
generate:

- `3000-kosakata-inggris-debug-apk`
- `3000-kosakata-inggris-release-aab-unsigned`

Before changing UI, confirm the latest `Android Build` workflow is green. Do not
hide or bypass build/test failures.

## App Identity

- App name: 3000 Kosakata Inggris
- Package/applicationId/namespace: `com.kosakata.inggris`
- compileSdk/targetSdk: 36
- minSdk: 23
- versionCode: 1
- versionName: 1.0.0

## Goal

Polish UI/UX without changing the stable local-first architecture or removing
features.

## UI Tasks

1. Create a consistent Material 3 design system:
   - color scheme
   - typography
   - spacing
   - card and button shapes
   - light and dark theme
2. Improve onboarding hierarchy and goal selection.
3. Improve Home dashboard cards and progress visualization.
4. Improve category browsing while preserving all 16 categories.
5. Improve flashcard readability and button placement.
6. Improve listening controls and current-word feedback.
7. Improve quiz answer feedback and result presentation.
8. Improve review and bookmark empty states.
9. Improve profile statistics and settings grouping.
10. Add loading and error states where needed.
11. Review accessibility, contrast, touch targets, and scalable text.
12. Verify small phones, large phones, and landscape behavior.

## Must Preserve

- all 11 required screens
- Room, DataStore, ViewModel, TTS, and AdMob
- `app/src/main/assets/vocabulary_words.json`
- due-review-first learning logic
- review scheduler behavior
- current-session quiz behavior
- banner ads only on Home, Profile, and Quiz Result
- no ads during flashcards, active quiz, or listening
- only `INTERNET` and `ACCESS_NETWORK_STATE` permissions

## Restrictions

- Do not add login, backend, Firebase, subscription, or payment.
- Do not change package name or app name.
- Do not replace the project with a new scaffold.
- Do not use Oxford branding.
- Do not add unnecessary permissions.
- Do not use experimental dependencies only for visual polish.

After UI changes:

1. Run unit tests.
2. Build debug APK and release AAB.
3. Push changes on a `codex/ui-polish` branch.
4. Confirm GitHub Actions succeeds.
5. Report screenshots or a concise visual change summary.
