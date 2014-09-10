# eve

FIXME

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2014 FIXME

industryActivity               industryActivityRaces
industryActivityMaterials      industryActivitySkills
industryActivityProbabilities  industryBlueprints
industryActivityProducts

sqlite> .schema dgmAttributeTypes
CREATE TABLE "dgmAttributeTypes" (
  "attributeID" integer NOT NULL,
  "attributeName" varchar(100) DEFAULT NULL,
  "description" varchar(1000) DEFAULT NULL,
  "iconID" integer DEFAULT NULL,
  "defaultValue" double DEFAULT NULL,
  "published" integer DEFAULT NULL,
  "displayName" varchar(100) DEFAULT NULL,
  "unitID" integer  DEFAULT NULL,
  "stackable" integer DEFAULT NULL,
  "highIsGood" integer DEFAULT NULL,
  "categoryID" integer  DEFAULT NULL,
  PRIMARY KEY ("attributeID")
);

sqlite> .schema dgmTypeAttributes
CREATE TABLE "dgmTypeAttributes" (
  "typeID" integer NOT NULL,
  "attributeID" integer NOT NULL,
  "valueInt" integer DEFAULT NULL,
  "valueFloat" double DEFAULT NULL,
  PRIMARY KEY ("typeID","attributeID")
);

sqlite> .schema dgmTypeEffects
CREATE TABLE "dgmTypeEffects" (
  "typeID" integer NOT NULL,
  "effectID" integer NOT NULL,
  "isDefault" integer DEFAULT NULL,
  PRIMARY KEY ("typeID","effectID")
);

sqlite> .schema industryActivity
CREATE TABLE "industryActivity" (
  "typeID" integer NOT NULL DEFAULT '0',
  "time" integer DEFAULT NULL,
  "activityID" integer NOT NULL DEFAULT '0',
  PRIMARY KEY ("typeID","activityID")
);
CREATE INDEX "industryActivity_activityID" ON "industryActivity" ("activityID");

sqlite> .schema industryActivityMaterials
CREATE TABLE "industryActivityMaterials" (
  "typeID" integer DEFAULT NULL,
  "activityID" integer DEFAULT NULL,
  "materialTypeID" integer DEFAULT NULL,
  "quantity" integer DEFAULT NULL,
  "consume" integer DEFAULT NULL
);
CREATE INDEX "industryActivityMaterials_typeID" ON "industryActivityMaterials" ("typeID");
CREATE INDEX "industryActivityMaterials_typeID_2" ON "industryActivityMaterials" ("typeID","activityID");

sqlite> .schema industryActivityProducts
CREATE TABLE "industryActivityProducts" (
  "typeID" integer DEFAULT NULL,
  "activityID" integer DEFAULT NULL,
  "productTypeID" integer DEFAULT NULL,
  "quantity" integer DEFAULT NULL
);
CREATE INDEX "industryActivityProducts_typeID" ON "industryActivityProducts" ("typeID");
CREATE INDEX "industryActivityProducts_typeID_2" ON "industryActivityProducts" ("typeID","activityID");
CREATE INDEX "industryActivityProducts_productTypeID" ON "industryActivityProducts" ("productTypeID");


sqlite> .schema industryActivityProbabilities
CREATE TABLE "industryActivityProbabilities" (
  "typeID" integer DEFAULT NULL,
  "activityID" integer DEFAULT NULL,
  "productTypeID" integer DEFAULT NULL,
  "probability" decimal(3,2) DEFAULT NULL
);
CREATE INDEX "industryActivityProbabilities_typeID" ON "industryActivityProbabilities" ("typeID");
CREATE INDEX "industryActivityProbabilities_typeID_2" ON "industryActivityProbabilities" ("typeID","activityID");
CREATE INDEX "industryActivityProbabilities_productTypeID" ON "industryActivityProbabilities" ("productTypeID");

sqlite> .schema industryActivityRaces
CREATE TABLE "industryActivityRaces" (
  "typeID" integer DEFAULT NULL,
  "activityID" integer DEFAULT NULL,
  "productTypeID" integer DEFAULT NULL,
  "raceID" integer DEFAULT NULL
);
CREATE INDEX "industryActivityRaces_typeID" ON "industryActivityRaces" ("typeID");
CREATE INDEX "industryActivityRaces_typeID_2" ON "industryActivityRaces" ("typeID","activityID");
CREATE INDEX "industryActivityRaces_productTypeID" ON "industryActivityRaces" ("productTypeID");

sqlite> .schema industryActivitySkills
CREATE TABLE "industryActivitySkills" (
  "typeID" integer DEFAULT NULL,
  "activityID" integer DEFAULT NULL,
  "skillID" integer DEFAULT NULL,
  "level" integer DEFAULT NULL
);
CREATE INDEX "industryActivitySkills_typeID" ON "industryActivitySkills" ("typeID");
CREATE INDEX "industryActivitySkills_typeID_2" ON "industryActivitySkills" ("typeID","activityID");


sqlite> .schema industryBlueprints
CREATE TABLE "industryBlueprints" (
  "typeID" integer NOT NULL,
  "maxProductionLimit" integer DEFAULT NULL,
  PRIMARY KEY ("typeID")
);

sqlite> .schema invTypes
CREATE TABLE "invTypes" (
  "typeID" integer NOT NULL,
  "groupID" integer DEFAULT NULL,
  "typeName" varchar(100) DEFAULT NULL,
  "description" varchar(3000) DEFAULT NULL,
  "mass" double DEFAULT NULL,
  "volume" double DEFAULT NULL,
  "capacity" double DEFAULT NULL,
  "portionSize" integer DEFAULT NULL,
  "raceID" integer  DEFAULT NULL,
  "basePrice" decimal(19,4) DEFAULT NULL,
  "published" integer DEFAULT NULL,
  "marketGroupID" integer DEFAULT NULL,
  "chanceOfDuplicating" double DEFAULT NULL,
  PRIMARY KEY ("typeID")
);
CREATE INDEX "invTypes_invTypes_IX_Group" ON "invTypes" ("groupID");

sqlite> .schema ramActivities
CREATE TABLE "ramActivities" (
  "activityID" integer  NOT NULL,
  "activityName" varchar(100) DEFAULT NULL,
  "iconNo" varchar(5) DEFAULT NULL,
  "description" varchar(1000) DEFAULT NULL,
  "published" integer DEFAULT NULL,
  PRIMARY KEY ("activityID")
);

sqlite> .schema ramAssemblyLineStations
CREATE TABLE "ramAssemblyLineStations" (
  "stationID" integer NOT NULL,
  "assemblyLineTypeID" integer  NOT NULL,
  "quantity" integer  DEFAULT NULL,
  "stationTypeID" integer DEFAULT NULL,
  "ownerID" integer DEFAULT NULL,
  "solarSystemID" integer DEFAULT NULL,
  "regionID" integer DEFAULT NULL,
  PRIMARY KEY ("stationID","assemblyLineTypeID")
);
CREATE INDEX "ramAssemblyLineStations_ramAssemblyLineStations_IX_region" ON "ramAssemblyLineStations" ("regionID");
CREATE INDEX "ramAssemblyLineStations_ramAssemblyLineStations_IX_owner" ON "ramAssemblyLineStations" ("ownerID");

-----------------------------------



agtAgentTypes                         invNames
agtAgents                             invPositions
agtResearchAgents                     invTraits
certCerts                             invTypeMaterials
certMasteries                         invTypeReactions
certSkills                            invTypes
chrAncestries                         invUniqueNames
chrAttributes                         mapCelestialStatistics
chrBloodlines                         mapConstellationJumps
chrFactions                           mapConstellations
chrRaces                              mapDenormalize
crpActivities                         mapJumps
crpNPCCorporationDivisions            mapLandmarks
crpNPCCorporationResearchFields       mapLocationScenes
crpNPCCorporationTrades               mapLocationWormholeClasses
crpNPCCorporations                    mapRegionJumps
crpNPCDivisions                       mapRegions
dgmAttributeCategories                mapSolarSystemJumps
dgmAttributeTypes                     mapSolarSystems
dgmEffects                            mapUniverse
dgmTypeAttributes                     planetSchematics
dgmTypeEffects                        planetSchematicsPinMap
eveIcons                              planetSchematicsTypeMap
eveUnits                              ramActivities
industryActivity                      ramAssemblyLineStations
industryActivityMaterials             ramAssemblyLineTypeDetailPerCategory
industryActivityProbabilities         ramAssemblyLineTypeDetailPerGroup
industryActivityProducts              ramAssemblyLineTypes
industryActivityRaces                 ramInstallationTypeContents
industryActivitySkills                staOperationServices
industryBlueprints                    staOperations
invCategories                         staServices
invContrabandTypes                    staStationTypes
invControlTowerResourcePurposes       staStations
invControlTowerResources              translationTables
invFlags                              trnTranslationColumns
invGroups                             trnTranslationLanguages
invItems                              trnTranslations
invMarketGroups                       warCombatZoneSystems
invMetaGroups                         warCombatZones
invMetaTypes



select * from ramActivities;
activityID|activityName|iconNo|description|published
0|None||No activity|1
1|Manufacturing|18_02|Manufacturing|1
2|Researching Technology|33_02|Technological research|0
3|Researching Time Efficiency|33_02|Researching time efficiency|1
4|Researching Material Efficiency|33_02|Researching material efficiency|1
5|Copying|33_02|Copying|1
6|Duplicating||The process of creating an item, by studying an already existing item.|0
7|Reverse Engineering|33_02|The process of creating a blueprint from an item.|1
8|Invention|33_02|The process of creating a more advanced item based on an existing item|1


sqlite> select * from chrRaces;
raceID|raceName
1|Caldari
2|Minmatar
4|Amarr
8|Gallente
16|Jove
32|Pirate
64|Sleepers
128|ORE
