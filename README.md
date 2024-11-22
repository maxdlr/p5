# Yoga App !
For launch and generate the jacoco code coverage:
`mvn clean test`

# Written tests
## Authentication
`AuthControllerTests`
### Login
- [x] La connexion
- [x] La gestion des erreurs en cas de mauvais login / password
- [x] L’affichage d’erreur en l’absence d’un champ obligatoire


### Register
- [x] La création de compte
- [x] L’affichage d’erreur en l’absence d’un champ obligatoire

## Sessions
`SessionControllerTests`
`TeacherControllerTests`
### Affichage
- [x] Affichage de la liste des sessions
- [x] L’apparition des boutons Create et Detail si l’utilisateur connecté est un admin

### Informations session
- [x] Les informations de la session sont correctement affichées
- [x] Le bouton Delete apparaît si l'utilisateur connecté est un admin

### Création session
- [x] La session est créée
- [x] L’affichage d’erreur en l’absence d’un champ obligatoire

### Suppression session
- [x] La session est correctement supprimée 

### Modification session
- [x] La session est modifiée
- [x] L’affichage d’erreur en l’absence d’un champ obligatoire

## User
`UserControllerTests`
### Account
- [x] Affichage des informations de l’utilisateur

### Logout
- [x] La déconnexion de l’utilisateur

