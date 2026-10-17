# java static analyzer
🇬🇧 [English](#english) · 🇫🇷 [Français](#français)
 
---
 
<a name="english"></a>
## 🇬🇧 English
 
**Command-line static analyzer for Java projects, built on Eclipse JDT.**
It reads the source code of a Java project (without running it), extracts its structure and method calls, computes code metrics and builds the call graph.
 
### Why this project?
 
Before evolving or refactoring a piece of software, you first have to understand it. This tool automates that first step: given an unfamiliar Java codebase, it tells you what it contains, how big its classes are, and who calls whom.
 
### Features
 
- **Structure extraction**: packages, classes, interfaces, enums, records (including nested types), attributes (type, visibility), methods and constructors, full superclass chain and interfaces.
- **Call extraction with type resolution**: every method invocation, with the static type of the receiver, the resolved target method and the line number. Unresolved calls are reported, never silently dropped.
- **13 code metrics**: number of classes / methods / packages, lines of code, averages per class and per method, top 10 % of classes by methods and by attributes, classes above a threshold `X`, top 10 % longest methods per class, maximum number of parameters.
- **Call graph**: nodes = project methods, edges = resolved calls weighted by number of call sites. Query **callees** and **callers** of any method (basis for impact analysis).
- **Robust CLI**: clear error messages and non-zero exit codes for missing folders, projects without Java files, malformed threshold, and files that do not compile (the rest is still analyzed).
- **Deterministic output**: running the tool twice on the same project gives exactly the same result.
### Architecture
 
```
.java files
    │
    ▼
Model extractor (ASTParser, bindings)
    │   AST + bindings
    ▼
Property extractors (ASTVisitor)
    │
    ▼
Fact model (own classes, JDT-independent)
    │
    ├──► Metrics
    └──► Call graph
            │
            ▼
        CLI output
```
 
The metrics and the call graph work **only** on the fact model: no `org.eclipse.jdt` class leaks beyond the extraction layer. This keeps each component independently testable.
 
Suggested package layout (adapt to your project):
 
| Package   | Role                                              |
|-----------|---------------------------------------------------|
| `extract` | JDT parsing, visitors, bindings                   |
| `model`   | Fact model: types, fields, methods, calls         |
| `metrics` | The 13 metrics                                    |
| `graph`   | Call graph, callers / callees                     |
| `cli`     | Argument parsing, output, error handling          |
 
### Tech stack
 
- Java 17+ (developed with Java 19)
- Maven 3.9
- Eclipse JDT Core 3.46.0
### Build
 
```bash
git clone https://github.com/<your-username>/java_static_analyzer.git
cd java_static_analyzer
mvn clean package
```
 
### Usage
 
```bash
java -jar target/<your-jar-name>.jar <project-path> [options]
```
 
Example (adapt the options to your actual CLI):
 
```bash
# structure
java -jar target/<your-jar-name>.jar ./my-project --structure
# calls with receiver types
java -jar target/<your-jar-name>.jar ./my-project --calls
# metrics, with threshold X = 5
java -jar target/<your-jar-name>.jar ./my-project --metrics --threshold 5
# call graph
java -jar target/<your-jar-name>.jar ./my-project --graph
# callers / callees of a method
java -jar target/<your-jar-name>.jar ./my-project --callers "library.service.Catalog#add(library.model.Item)"
```
 
Paths containing spaces must be quoted.
 
### Example output
 
```
(paste a short real output here once your tool works)
```
 
### Known limitations
 
This is a **static approximation**. The call graph does not capture reflection, proxies or dependency injection, and it does not tell which overriding method will actually run at runtime (virtual calls are not expanded). It answers the question: *"does method f name method g in its code?"*
 
### Context
 
Developed as part of the *Software Evolution and Restructuring* course (HAI913I), Master 2 Software Engineering, Université de Montpellier.
 
---
 
<a name="français"></a>
## 🇫🇷 Français
 
**Analyseur statique de code Java en ligne de commande, basé sur Eclipse JDT.**
Il lit le code source d'un projet Java (sans l'exécuter), en extrait la structure et les appels de méthodes, calcule des métriques et construit le graphe d'appel.
 
### Pourquoi ce projet ?
 
Avant de faire évoluer ou de restructurer un logiciel, il faut d'abord le comprendre. Cet outil automatise cette première étape : sur un code Java inconnu, il indique ce qu'il contient, la taille de ses classes et qui appelle qui.
 
### Fonctionnalités
 
- **Extraction de la structure** : paquetages, classes, interfaces, énumérations, records (y compris types imbriqués), attributs (type, visibilité), méthodes et constructeurs, chaîne complète des superclasses et interfaces.
- **Extraction des appels avec résolution des types** : chaque appel de méthode, avec le type statique du receveur, la méthode cible résolue et la ligne. Les appels non résolus sont signalés, jamais ignorés en silence.
- **13 métriques** : nombre de classes / méthodes / paquetages, lignes de code, moyennes par classe et par méthode, 10 % des classes ayant le plus de méthodes et d'attributs, classes au-dessus d'un seuil `X`, 10 % des méthodes les plus longues par classe, nombre maximal de paramètres.
- **Graphe d'appel** : nœuds = méthodes du projet, arcs = appels résolus pondérés par le nombre de sites d'appel. Interrogation des **appelées** et des **appelantes** de toute méthode (base de l'analyse d'impact).
- **Ligne de commande robuste** : messages clairs et code de sortie non nul pour un dossier inexistant, un projet sans fichier Java, un seuil mal formé ; les fichiers qui ne compilent pas sont signalés et le reste est analysé.
- **Sortie déterministe** : deux exécutions sur le même projet donnent exactement le même résultat.
### Architecture
 
```
fichiers .java
    │
    ▼
Extracteur de modèle (ASTParser, bindings)
    │   AST + bindings
    ▼
Extracteurs de propriétés (ASTVisitor)
    │
    ▼
Modèle de faits (classes propres, indépendant de JDT)
    │
    ├──► Métriques
    └──► Graphe d'appel
            │
            ▼
        Sortie en ligne de commande
```
 
Les métriques et le graphe d'appel travaillent **uniquement** sur le modèle de faits : aucune classe `org.eclipse.jdt` ne sort de la couche d'extraction. Chaque composant peut ainsi être testé séparément.
 
| Paquetage | Rôle                                                  |
|-----------|-------------------------------------------------------|
| `extract` | Analyse JDT, visiteurs, bindings                      |
| `model`   | Modèle de faits : types, attributs, méthodes, appels  |
| `metrics` | Les 13 métriques                                      |
| `graph`   | Graphe d'appel, appelantes / appelées                 |
| `cli`     | Arguments, affichage, gestion des erreurs             |
 
### Technologies
 
- Java 17+ (développé avec Java 19)
- Maven 3.9
- Eclipse JDT Core 3.46.0
### Construction
 
```bash
git clone https://github.com/<votre-utilisateur>/java_static_analyzer.git
cd java_static_analyzer
mvn clean package
```
 
### Utilisation
 
```bash
java -jar target/<nom-du-jar>.jar <chemin-du-projet> [options]
```
 
Exemples (à adapter à votre ligne de commande) :
 
```bash
# structure
java -jar target/<nom-du-jar>.jar ./mon-projet --structure
# appels avec types des receveurs
java -jar target/<nom-du-jar>.jar ./mon-projet --calls
# métriques, avec seuil X = 5
java -jar target/<nom-du-jar>.jar ./mon-projet --metrics --threshold 5
# graphe d'appel
java -jar target/<nom-du-jar>.jar ./mon-projet --graph
# appelantes / appelées d'une méthode
java -jar target/<nom-du-jar>.jar ./mon-projet --callers "library.service.Catalog#add(library.model.Item)"
```
 
Les chemins contenant des espaces doivent être entre guillemets.
 
### Exemple de sortie
 
```
(coller ici une courte sortie réelle quand l'outil fonctionne)
```
 
### Limites connues
 
C'est une **approximation statique**. Le graphe d'appel ne capture ni la réflexion, ni les proxys, ni l'injection de dépendances, et ne dit pas quelle redéfinition sera exécutée (les appels virtuels ne sont pas expansés). Il répond à la question : *« la méthode f nomme-t-elle la méthode g dans son code ? »*
 
### Contexte
 
Réalisé dans le cadre du module *Évolution et restructuration des logiciels* (HAI913I), Master 2 Génie Logiciel, Université de Montpellier.
