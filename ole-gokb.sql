# -----------------------------------------------------------------------
# OLE_DESC_EXT_DATASRC_T
# -----------------------------------------------------------------------
drop table if exists OLE_DESC_OAIPMH_DATASRC_T;

CREATE TABLE OLE_DESC_OAIPMH_DATASRC_T
(
      OAI_ID DECIMAL(8) default 0
        , OAI_NAME VARCHAR(40) NOT NULL
        , OAI_DESC VARCHAR(100)
        , OAI_BASE_URL VARCHAR(255)
        , OAI_METADATA_PREFIX VARCHAR(40)
        , OAI_SET_NAME VARCHAR(40)
        , OAI_TIMESTAMP_CURSOR VARCHAR(40)
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
    , CONSTRAINT OLE_DESC_OAIPMH_DATASRC_TP1 PRIMARY KEY(OAI_ID)
    , CONSTRAINT DS_NAME UNIQUE (OAI_NAME)

) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin;

drop table if exists ole_desc_oaipmh_datasrc_s;

CREATE TABLE `ole_desc_oaipmh_datasrc_s` (
  `id` bigint(19) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

drop table if exists OLE_GOKB_PKG_T;

CREATE TABLE OLE_GOKB_PKG_T
(
      PKG_ID INT(11) COLLATE utf8_bin NOT NULL DEFAULT 0
        , PKG_NAME VARCHAR(100) NOT NULL
        , PKG_IDENTIFIER VARCHAR(100) NOT NULL
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
        , PKG_CONTENT LONGBLOB
        , TITLE_COUNT DECIMAL(8)
        , GLOBAL_STATUS VARCHAR(15)
        , LOCAL_STATUS VARCHAR(15)
        , PRIMARY_PLAT VARCHAR(45)
        , PRIMARY_PLAT_PROV VARCHAR(45)
        , DATE_CREATED TIMESTAMP
        , LAST_UPDATED TIMESTAMP
    , CONSTRAINT OLE_GOKB_PKG_TP1 PRIMARY KEY(PKG_ID)
    , CONSTRAINT PKG_IDENTIFIER_UNIQUE UNIQUE (PKG_IDENTIFIER)
) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin;


drop table if exists ole_desc_oaipmh_datasrc_s;

CREATE TABLE `ole_desc_oaipmh_datasrc_s` (
  `id` bigint(19) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

drop table if exists OLE_GOKB_PKG_S;

CREATE TABLE `OLE_GOKB_PKG_S` (
  `id` bigint(19) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


insert into ole_gokb_pkg_s values (0);


CREATE TABLE OLE_GOKB_TIPP_T
(
      TIPP_ID INT(11) COLLATE utf8_bin NOT NULL DEFAULT 0
        , OBJ_ID VARCHAR(36) NOT NULL
        , VER_NBR DECIMAL(8) default 1 NOT NULL
        , ISBN varchar(40) COLLATE utf8_bin DEFAULT NULL
        , ISSN varchar(40) COLLATE utf8_bin DEFAULT NULL
        , EISSN varchar(40) COLLATE utf8_bin DEFAULT NULL
        , DOI varchar(100) COLLATE utf8_bin DEFAULT NULL
        , GOKB_PKG_FK INT(11) NOT NULL
        , ACCESS_URL varchar(800) COLLATE utf8_bin DEFAULT NULL
        , COV_START_DATE datetime DEFAULT NULL
        , COV_START_VOL varchar(40) COLLATE utf8_bin DEFAULT NULL
        , COV_START_ISS varchar(40) COLLATE utf8_bin DEFAULT NULL
        , COV_END_DATE datetime DEFAULT NULL
        , COV_END_VOL varchar(40) COLLATE utf8_bin DEFAULT NULL
        , COV_END_ISS varchar(40) COLLATE utf8_bin DEFAULT NULL
        , DATE_CREATED TIMESTAMP
        , LAST_UPDATED TIMESTAMP
    , CONSTRAINT OLE_GOKB_TIPP_TP1 PRIMARY KEY(TIPP_ID)
    , CONSTRAINT GOKB_PKG_FK FOREIGN KEY (GOKB_PKG_FK)  REFERENCES OLE_GOKB_PKG_T ( PKG_ID )
) ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin;

drop table if exists OLE_GOKB_TIPP_S;

CREATE TABLE `OLE_GOKB_TIPP_S` (
  `id` bigint(19) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

insert into ole_gokb_tipp_s values (0);

