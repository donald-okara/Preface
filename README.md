# Preface
<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin" height="30"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat&logo=android&logoColor=white" alt="Jetpack Compose" height="30"/>
  <img src="https://img.shields.io/badge/Hilt-34A853?style=flat&logo=android&logoColor=white" alt="Hilt" height="30"/>
  <img src="https://img.shields.io/badge/Supabase-3ECF8E?style=flat&logo=supabase&logoColor=white" alt="Supabase" height="30"/>
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=firebase&logoColor=black" alt="Firebase" height="30"/>
</p>

Preface is a bookshelf app built by a lazy reader (myself) as an accountability tool and bookmark for his reading progress.

### 🖲️ Key features
- Online/Offline persisted bookshelves
- Reading progress
- Book search powered by Google books api (If it is published, you will find it)

### 📱 Screenshots
<p align="center">
  <img src="https://github.com/user-attachments/assets/4669ef00-d28b-4826-9f52-bad7733d43f5" width="150"/>
  <img src="https://github.com/user-attachments/assets/5db92ace-2cef-4565-8846-c721bd83de2a" width="150"/>
  <img src="https://github.com/user-attachments/assets/b33d7bc4-9e5f-4ba7-8a5c-7c5c5454a18e" width="150"/>
  <img src="https://github.com/user-attachments/assets/e708ad2c-5dce-48c5-bebe-acc5ac8267e4" width="150"/>
  <img src="https://github.com/user-attachments/assets/476b1533-22fb-4adb-aed5-c4d0044f1ccf" width="150"/>
</p>

### 💻 Contributions
Appreciate the project? Here's how you can help:

- 🌟 Star : Give it a star at the top right. It means a lot!
- 😎 Contribute : Found an issue or have a feature idea? Submit a PR.
- 💬 Feedback : Have suggestions? Open an issue or start a discussion.

### Module Graph

```mermaid
%%{
  init: {
    'theme': 'base',
    'themeVariables': {"primaryTextColor":"#ffffff","primaryColor":"#008080","primaryBorderColor":"#006666","lineColor":"#00b3b3","tertiaryColor":"#004c4c","fontSize":"12px"}
  }
}%%

graph LR
  :feature-book-details["feature-book-details"]
  :shared-components["shared-components"]
  :common-domain["common-domain"]
  :common-datasource["common-datasource"]
  :feature-profile["feature-profile"]
  :feature-authentication["feature-authentication"]
  :common-datasource["common-datasource"]
  :shared-components["shared-components"]
  :feature-authentication["feature-authentication"]
  :feature-bookshelf["feature-bookshelf"]
  :app["app"]
  :common-navigation["common-navigation"]
  :common-navigation["common-navigation"]
  :feature-book-details["feature-book-details"]
  :feature-bookshelf["feature-bookshelf"]
  :feature-profile["feature-profile"]

  :feature-book-details --> :shared-components
  :feature-book-details --> :common-domain
  :feature-book-details --> :common-datasource
  :feature-profile --> :feature-authentication
  :feature-profile --> :common-domain
  :feature-profile --> :shared-components
  :feature-profile --> :common-datasource
  :common-datasource --> :common-domain
  :shared-components --> :common-domain
  :feature-authentication --> :common-datasource
  :feature-authentication --> :common-domain
  :feature-bookshelf --> :common-domain
  :feature-bookshelf --> :common-datasource
  :feature-bookshelf --> :shared-components
  :app --> :common-navigation
  :app --> :common-domain
  :app --> :shared-components
  :app --> :common-datasource
  :common-navigation --> :common-datasource
  :common-navigation --> :shared-components
  :common-navigation --> :common-domain
  :common-navigation --> :feature-book-details
  :common-navigation --> :feature-authentication
  :common-navigation --> :feature-bookshelf
  :common-navigation --> :feature-profile

classDef android-library fill:#3BD482,stroke:#fff,stroke-width:2px,color:#fff;
classDef android-application fill:#2C4162,stroke:#fff,stroke-width:2px,color:#fff;
class :feature-book-details android-library
class :shared-components android-library
class :common-domain android-library
class :common-datasource android-library
class :feature-profile android-library
class :feature-authentication android-library
class :feature-bookshelf android-library
class :app android-application
class :common-navigation android-library

```