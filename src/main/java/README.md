# Parcours "Au minimum"

Ce dossier Java contient les cinq étapes du projet PadChest. Chaque étape propose plusieurs niveaux, mais ce guide se concentre sur les implémentations marquées **minimum** afin de rappeler ce que couvre exactement le niveau « Au minimum » et comment l'enchaîner rapidement.

## Préparation commune

- Placez le fichier CSV original dans `src/main/java/data/` ou indiquez son chemin lors de l'exécution.
- Compilez une fois tout le projet avec `mvn -q compile`; les classes seront disponibles dans `target/classes`.
- Toutes les commandes ci-dessous supposent que vous restez à la racine du projet lorsque vous lancez `java -cp target/classes ...`.

## Étape 1 – Conversion CSV → XML (minimum)

- Classe : `step1_csv_to_xml.minimum.CSVToXML`.
- Lecture ligne par ligne d'un CSV séparé par des virgules, sans gestion des guillemets ni des listes imbriquées.
- Production d'un XML minimal contenant pour chaque image `id`, `patientId` et `viewPosition`, avec une DOCTYPE pointant vers la DTD minimum.
- Exécution par défaut :
  ```
  java -cp target/classes step1_csv_to_xml.minimum.CSVToXML \
       src/main/java/data/PADCHEST_chest_x_ray_images_labels_160K_01.02.19.csv \
       src/main/java/step1_csv_to_xml/minimum/output.xml
  ```
- Vous pouvez rediriger la sortie XML en passant un second argument.

## Étape 2 – Structure XML (minimum)

- Fichier : `step2_structures/minimum/images.dtd`.
- Définit l'enchaînement `images → image*`, avec chaque `image` qui contient `patientId`, `viewPosition`, puis éventuellement des blocs `labels` et `localizations`.
- Attribut obligatoire `id` sur chaque `image` pour identifier l'examen.
- Utilisé automatiquement lorsque le XML généré par l'étape 1 inclut la directive DOCTYPE minimale.

## Étape 3 – Validation et statistiques (minimum)

- Classe : `step3_validation_parser.minimum.ValidateAndStats`.
- Parseur SAX validant le XML minimum via la DTD ; messages d'erreur bloquants en cas d'incohérence.
- Calcul des deux statistiques exigées : nombre d'images où la localisation vaut `loc right` et top N des libellés (10 par défaut).
- Commande recommandée :
  ```
  java -cp target/classes step3_validation_parser.minimum.ValidateAndStats \
       src/main/java/step1_csv_to_xml/minimum/output.xml
  ```
- La classe résout automatiquement la DTD si le fichier se trouve dans la hiérarchie du projet.

## Étape 4 – Génération du site HTML (minimum)

- Classe : `step4_xslt_site.minimum.ApplyXSLT` avec la feuille `step4_xslt_site/minimum/style.xsl`.
- Transforme le XML minimum en `output.html`, une table paginée (50 lignes par page) avec pagination JavaScript prête à l'emploi.
- Invocation type :
  ```
  java -cp target/classes step4_xslt_site.minimum.ApplyXSLT \
       src/main/java/step1_csv_to_xml/minimum/output.xml \
       src/main/java/step4_xslt_site/minimum/style.xsl \
       src/main/java/step4_xslt_site/minimum/output.html
  ```
- Ouvrez ensuite le fichier HTML généré dans un navigateur pour vérifier la mise en page.

## Étape 5 – Requêtes XQuery (minimum)

- Dossier : `step5_xquery/minimum/` (vide pour l'instant).
- À compléter avec vos requêtes XQuery simples dès que la structure XML finale est stabilisée.
- Maven fournit déjà Saxon-HE et BaseX pour exécuter ou tester ces requêtes ; ajoutez vos scripts `.xq` et documentez la manière de les lancer.

## Pipeline rapide « minimum »

1. Conversion : exécuter `CSVToXML` (étape 1).
2. Validation et statistiques : exécuter `ValidateAndStats` (étape 3) sur le XML produit.
3. Publication : exécuter `ApplyXSLT` (étape 4) et ouvrir le HTML généré.
4. Optionnel : ajouter et tester vos requêtes XQuery (étape 5) dès qu'elles sont disponibles.

Cette check-list couvre l'intégralité du parcours « Au minimum » décrit dans l'énoncé et correspond exactement aux fonctionnalités présentes dans le code source.

