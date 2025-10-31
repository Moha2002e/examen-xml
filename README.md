# Projet PadChest – Conversion et manipulation XML en Java

Ce projet est un squelette pour le travail pratique décrit dans le document **EnonceXML.pdf**.  Il est organisé en plusieurs étapes, chacune correspondant à un dossier séparé.  Le but est de vous guider à travers la transformation d’un fichier CSV volumineux (données PadChest) en un document XML valide, la définition d’une structure (DTD/XSD), la validation et l’analyse des données via un parseur Java, la génération d’une page Web via XSLT et l’exécution de requêtes XQuery.

## Arborescence

Le projet est divisé en cinq étapes (**step1** à **step5**).  Chacune de ces étapes
contient trois sous‑répertoires nommés **minimum**, **pro** et **expert**.  Ces
sous‑répertoires correspondent aux différents niveaux d’exigence décrits dans
l’énoncé : « Au minimum », « Pour les pros » et « Pour les experts »【288334792020038†L90-L156】.

```
project/
├── README.md
├── build.sh                       – Script de compilation (facultatif)
├── step1_csv_to_xml/
│   ├── minimum/                   – Convertisseur CSV→XML simple (niveau minimum)
│   │   └── CSVToXML.java
│   ├── pro/                       – Convertisseur CSV→XML robuste (niveau pro)
│   │   └── CSVToXML.java
│   └── expert/                    – Convertisseur CSV→XML avec mesure de performances (niveau expert)
│       └── CSVToXML.java
├── step2_structures/
│   ├── minimum/
│   │   └── images.dtd            – DTD minimal décrivant la structure XML
│   ├── pro/
│   │   └── images.xsd            – Schéma XSD pour une validation plus stricte
│   └── expert/
│       └── images_extended.xsd   – XSD enrichi avec contraintes supplémentaires
├── step3_validation_parser/
│   ├── minimum/
│   │   └── ValidateAndStats.java – Parseur SAX validant via DTD et calculant les statistiques
│   ├── pro/
│   │   └── ValidateAndStats.java – Parseur DOM validant via XSD et calculant les statistiques
│   └── expert/
│       └── PerformanceComparison.java – Programme comparant les performances SAX vs DOM
├── step4_xslt_site/
│   ├── minimum/
│   │   └── style.xsl             – Feuille XSLT basique générant une table HTML
│   ├── pro/
│   │   └── style.xsl             – Feuille XSLT améliorée (mise en page et détails interactifs)
│   └── expert/
│       └── style.xsl             – Feuille XSLT avec tri interactif et surlignage
├── step5_xquery/
│   ├── minimum/
│   │   └── queries.xq            – Deux requêtes XQuery simples
│   ├── pro/
│   │   └── queries.xq            – Requêtes encapsulées pour un service web XML
│   └── expert/
│       └── queries.xq            – Requêtes paramétrables retournant du JSON
└── data/ (à créer)               – Placez ici le fichier CSV d’entrée
```

### Étapes

1. **step1 – Conversion CSV→XML** : lire le fichier CSV PadChest et générer un fichier XML.  Trois implémentations sont proposées : une version minimale sans gestion des guillemets, une version pro qui gère les champs entre guillemets et les listes, et une version expert qui mesure également le temps et la mémoire utilisés.
2. **step2 – Structures XML** : définir la structure de votre document XML.  Le niveau minimum fournit une DTD, le niveau pro une XSD et le niveau expert un schéma plus détaillé avec des contraintes supplémentaires.
3. **step3 – Validation et statistiques** : vérifier la validité du fichier XML et calculer les statistiques (nombre d’images « loc right » et top labels).  La version minimale utilise un parseur SAX avec la DTD, la version pro un parseur DOM avec la XSD, et la version expert compare les performances des deux méthodes.
4. **step4 – Génération du site Web** : transformer le fichier XML en une page HTML via XSLT.  La version minimale affiche une table simple, la version pro propose une mise en page plus ergonomique avec des détails dépliables, et la version expert ajoute du tri interactif et du surlignage via JavaScript.
5. **step5 – Requêtes XQuery** : écrire des requêtes XQuery pour interroger votre base XML.  La version minimale contient deux requêtes basiques, la version pro encapsule les requêtes pour un service web XML, et la version expert illustre un service paramétrable retournant du JSON.

## Préparation des données

Téléchargez le fichier CSV **PADCHEST_chest_x_ray_images_labels_160K_01.02.19.csv** et placez‑le dans un dossier `data/` à la racine du projet (ou adaptez les chemins lors de l’exécution).  Ce fichier n’est pas fourni ici car il est volumineux.  Vous pouvez l’obtenir à partir de l’archive `data.zip` fournie avec l’énoncé.

## Compilation et exécution

Le projet n’utilise ni Maven ni Gradle ; chaque niveau peut être compilé séparément à l’aide de `javac`.  Exemple pour l’étape 1 :

```bash
# Compilation du convertisseur minimum
javac step1_csv_to_xml/minimum/CSVToXML.java
# Conversion minimale
java -cp step1_csv_to_xml/minimum CSVToXML data/PADCHEST_chest_x_ray_images_labels_160K_01.02.19.csv output_min.xml

# Compilation de la version pro
javac step1_csv_to_xml/pro/CSVToXML.java
# Conversion pro
java -cp step1_csv_to_xml/pro CSVToXML data/PADCHEST_chest_x_ray_images_labels_160K_01.02.19.csv output_pro.xml

# Compilation de la version expert
javac step1_csv_to_xml/expert/CSVToXML.java
# Conversion expert avec mesures de performances
java -cp step1_csv_to_xml/expert CSVToXML data/PADCHEST_chest_x_ray_images_labels_160K_01.02.19.csv output_expert.xml
```

Pour l’étape 3, compilez et exécutez la classe correspondant au niveau choisi :

```bash
# Validation et statistiques (niveau minimum)
javac step3_validation_parser/minimum/ValidateAndStats.java
java -cp step3_validation_parser/minimum ValidateAndStats output_pro.xml

# Validation DOM (niveau pro)
javac step3_validation_parser/pro/ValidateAndStats.java
java -cp step3_validation_parser/pro ValidateAndStats output_pro.xml

# Comparaison des performances (niveau expert)
javac step3_validation_parser/expert/PerformanceComparison.java
java -cp step3_validation_parser/expert PerformanceComparison output_pro.xml
```

Les feuilles XSLT des étapes 4 pro et expert généreront des pages HTML avec des fonctionnalités supplémentaires ; pour les visualiser, transformez votre fichier XML en HTML à l’aide d’un processeur XSLT (par exemple `xsltproc`).

## Notes importantes

- Le fichier CSV est très volumineux (~100 Mo).  Pensez à ajuster la mémoire allouée à la JVM si vous traitez toutes les lignes (paramètre `-Xmx`).  Pour tester rapidement la chaîne de traitement, vous pouvez limiter le nombre de lignes lues dans `CSVToXML.java` (par exemple en utilisant un compteur ou en fournissant un fichier CSV plus petit).
- Le projet fournit une DTD et un schéma XSD minimalistes pour illustrer la structure du document.  Vous êtes libre d’enrichir cette structure selon vos besoins tant que cela respecte les consignes de l’énoncé (utilisation d’attributs, etc.).
- Les requêtes XQuery dans `queries.xq` sont basées sur la structure XML proposée.  Si vous modifiez la structure, ajustez également les requêtes.
- La feuille XSLT génère une page HTML simple.  Vous pouvez l’améliorer (styles CSS, scripts JS) sans changer la logique de base.

## Références

Les exigences de ce projet sont décrites dans l’énoncé fourni par votre enseignant 【288334792020038†L90-L156】.  Cette arborescence et les fichiers proposés vous aident à structurer votre travail étape par étape.