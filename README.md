# KubiApp (taskList)

Application Android de gestion de taches construite avec Jetpack Compose.

## Fonctionnalites principales

- Affichage de la liste des taches sauvegardees localement.
- Ajout d'une tache (titre, description, date limite optionnelle).
- Validation a la creation (champs obligatoires et date limite valide).
- Marquage d'une tache comme terminee/non terminee depuis la liste.
- Consultation du detail d'une tache.
- Suppression d'une tache avec confirmation.
- Partage du detail d'une tache via l'intent Android.
- Ecran "Quote of the day" (recuperation d'une citation via API).

## Stack technique

- Kotlin
- Android SDK (minSdk 24, targetSdk 35, compileSdk 35)
- Jetpack Compose + Material 3
- Navigation Compose
- Room (stockage local)
- Retrofit (appel HTTP pour la citation)
- Coroutines + Flow

## Architecture (vue simple)

- `app/src/main/java/com/example/td2/data/local/` : entite Room, DAO, base de donnees.
- `app/src/main/java/com/example/td2/repository/` : interface repository + implementation locale.
- `app/src/main/java/com/example/td2/ui/viewmodel/` : logique UI (liste, ajout).
- `app/src/main/java/com/example/td2/ui/task/` : ecrans d'ajout et detail.
- `app/src/main/java/com/example/td2/ui/quote/` : ecran citation.
- `app/src/main/java/com/example/td2/navigation/` : routes et navigation Compose.

## Lancer le projet

### Prerequis

- Android Studio
- SDK Android configure (via `local.properties` en local)

### Demarrage

1. Ouvrir le dossier du projet dans Android Studio.
2. Laisser Gradle synchroniser les dependances.
3. Lancer l'application sur un emulateur ou un appareil Android.

## Build en ligne de commande (optionnel)

```powershell
.\gradlew.bat assembleDebug
```

## Notes

- Le stockage des taches est local (Room).
- La fonctionnalite citation utilise Internet (`android.permission.INTERNET`).

