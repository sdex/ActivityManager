## 5.4.13 (8 Feb 2025)
- add an option to configure the 'su' binary name #98
- fix bottom sheet dialogs expand/collapse states for landscape orientation
- fix preferences components' color to match the theme
- update translations

## 5.4.12 (1 Feb 2025)
- move app preferences to the dialog
- fix preferences dialog expand/collapse states for landscape orientation
- display number of application

## 5.4.11 (16 Mar 2024)
- @mikropsoft added a Turkish translation

## 5.4.10 (19 Feb 2024)
- update translations

## 5.4.9 (14 Feb 2024)
- update shortcut activity to match the app's style
- add more links to about screen
- update translations

## 5.4.8 (7 Feb 2024)
- avoid duplicates in a shortcut name suggestions
- add filter bottom sheet drag handle
- scroll the list to the top to display new items
- scroll the activities list to the top when toggle non-exported activities visibility
- hide the fast scroll popup when the list is sorted in non-alphabetical order
- handle the case when an application versionName is null
- add an option to create an application shortcut
- update snackbar style

## 5.4.7 (15 Jan 2024)
- Add a new filter menu with sorting options

## 5.4.6 (6 Jan 2024)
- Display applications version

## 5.4.5 (2 Oct 2023)
- Support per-app language preferences on Android 13+

## 5.4.4 (25 Sep 2023)
- Bugfixes

## 5.4.3 (18 Sep 2023)
- Add an option to launch an activity shortcut using root shell [#56](https://github.com/sdex/ActivityManager/issues/56)

## 5.4.2 (6 Sep 2023)
- Fix intent builder screen state reset [#52](https://github.com/sdex/ActivityManager/pull/52) (by [Jesse205](https://github.com/Jesse205))
- Add name suggestions when creating a shortcut [#53](https://github.com/sdex/ActivityManager/pull/53) (by [Jesse205](https://github.com/Jesse205))

## 5.4.1 (29 May 2023)
- Add a new translation - Portuguese, Brazilian (by [m_s_](https://crowdin.com/profile/askz0))

## 5.4.0 (28 Apr 2023)
- Add support for custom icons to intent shortcuts

## 5.3.5 (18 Apr 2023)
- Improve searching in manifest files [#37](https://github.com/sdex/ActivityManager/issues/37) (by [Y-E-P](https://github.com/Y-E-P))

## 5.3.4 (3 Apr 2023)
- Add a new translation - Chinese Simplified (by [Jesse205](https://crowdin.com/profile/Jesse205))
- Fix starting some activities from shortcuts [#32](https://github.com/sdex/ActivityManager/issues/32)

## 5.3.3 (25 Mar 2023)
- Add exporting the manifest file to the local storage

## 5.3.2 (13 Feb 2023)
- Add Spanish translation (kudos to [juanunzu](https://crowdin.com/profile/juanunzu))

## 5.3.1 (7 Feb 2023)
- Add an option to quickly switch the "Show not exported activities" preference from the activities list screen

## 5.3.0 (28 Jan 2023)
- Update [highlightjs](https://highlightjs.org/) to the latest version
- Remove unnecessary storage permissions
- Add show/hide line numbers toggle to manifest viewer
- Add Polish translation (#23)

## 5.2.1 (26 Nov 2022)
- Fix losing search state after dismissing the bottom dialog menu

## 5.2.0 (14 Nov 2022)
- Load activities of disabled apps

## 5.1.1 (23 Oct 2022)
- Make saving intent launch to history optional

## 5.1.0 (7 Oct 2022)
- Load manifest for bundles

## 5.0.0 (16 Sep 2022)
- Material 3
- Select a shortcut icon from the app
- Simplify root usage
- Show disabled apps
- Display an activity label when it's available
- Drop support for Android below 5.0
