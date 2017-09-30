Adds a teleport map table
CREATE TABLE `teleportmaps` (
  `id` int(11) NOT NULL auto_increment, `characterid` int(11) NOT NULL,
  `pos` smallint(2) NOT NULL,
  `source` tinyint(1) NOT NULL,
  `mapid` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;