-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Хост: 127.0.0.1
-- Время создания: Июн 29 2014 г., 15:03
-- Версия сервера: 5.5.25
-- Версия PHP: 5.2.12

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- База данных: `textmmo`
--

-- --------------------------------------------------------

--
-- Структура таблицы `accounts`
--

CREATE TABLE IF NOT EXISTS `accounts` (
  `login` text NOT NULL,
  `password` text NOT NULL,
  `mail` text NOT NULL,
  `entityId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `accounts`
--

INSERT INTO `accounts` (`login`, `password`, `mail`, `entityId`) VALUES
('Alastar', '1', 'some@mail.ru', 1),
('Alastar2', '1', 'some2@mail.ru', 2),
('Alastar3', '1', 'some@mail.ru', 3);

-- --------------------------------------------------------

--
-- Структура таблицы `attributes`
--

CREATE TABLE IF NOT EXISTS `attributes` (
  `name` text NOT NULL,
  `itemId` int(11) NOT NULL,
  `value` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `attributes`
--

INSERT INTO `attributes` (`name`, `itemId`, `value`) VALUES
('Durability', 6, 997),
('Durability', 2, 917);

-- --------------------------------------------------------

--
-- Структура таблицы `entities`
--

CREATE TABLE IF NOT EXISTS `entities` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `caption` text NOT NULL,
  `type` text NOT NULL,
  `locationId` int(11) NOT NULL,
  `ai` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Дамп данных таблицы `entities`
--

INSERT INTO `entities` (`id`, `caption`, `type`, `locationId`, `ai`) VALUES
(1, 'Alastar', 'Human', 3, ''),
(2, 'Zuzya', 'Elf', 1, ''),
(3, 'Kenzo', 'Orc', 1, '');

-- --------------------------------------------------------

--
-- Структура таблицы `inventories`
--

CREATE TABLE IF NOT EXISTS `inventories` (
  `entityId` int(11) NOT NULL,
  `max` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `inventories`
--

INSERT INTO `inventories` (`entityId`, `max`) VALUES
(1, 20),
(2, 20),
(3, 20);

-- --------------------------------------------------------

--
-- Структура таблицы `items`
--

CREATE TABLE IF NOT EXISTS `items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `locationId` int(11) NOT NULL,
  `caption` text NOT NULL,
  `amount` int(11) NOT NULL,
  `entityId` int(11) NOT NULL DEFAULT '-1',
  `type` text NOT NULL,
  `actionType` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=15 ;

--
-- Дамп данных таблицы `items`
--

INSERT INTO `items` (`id`, `locationId`, `caption`, `amount`, `entityId`, `type`, `actionType`) VALUES
(2, 1, 'Wooden pickaxe', 1, 1, 'None', 'Mine'),
(6, 3, 'Wooden axe', 1, 1, 'None', 'Cut'),
(7, 5, 'wheat', 4, 1, 'None', 'None'),
(8, 5, 'emerald', 7, 1, 'None', 'None'),
(9, 5, 'iron ore', 9, 1, 'None', 'None'),
(10, 5, 'ginseng', 9, 1, 'None', 'None'),
(11, 5, 'swiftstone', 3, 1, 'None', 'None'),
(12, 5, 'copper ore', 3, 1, 'None', 'None'),
(13, 1, 'Coin', 1, 3, 'None', 'None');

-- --------------------------------------------------------

--
-- Структура таблицы `knownspells`
--

CREATE TABLE IF NOT EXISTS `knownspells` (
  `spellName` text NOT NULL,
  `entityId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `knownspells`
--

INSERT INTO `knownspells` (`spellName`, `entityId`) VALUES
('heal', 1);

-- --------------------------------------------------------

--
-- Структура таблицы `locationflags`
--

CREATE TABLE IF NOT EXISTS `locationflags` (
  `locationId` int(11) NOT NULL,
  `flag` text NOT NULL,
  `val` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `locationflags`
--

INSERT INTO `locationflags` (`locationId`, `flag`, `val`) VALUES
(1, 'Start', ''),
(2, 'PVP', ''),
(3, 'Wood', ''),
(4, 'Wood', ''),
(5, 'Mine', ''),
(6, 'Mine', ''),
(3, 'Plough', ''),
(4, 'Plough', '');

-- --------------------------------------------------------

--
-- Структура таблицы `locations`
--

CREATE TABLE IF NOT EXISTS `locations` (
  `id` int(11) NOT NULL,
  `worldId` text NOT NULL,
  `name` text NOT NULL,
  `nearlocationsIDs` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `locations`
--

INSERT INTO `locations` (`id`, `worldId`, `name`, `nearlocationsIDs`) VALUES
(1, 'Primus', 'River', '4;3;5;6;'),
(2, 'Hell', 'Lava Lake', ''),
(3, 'Primus', 'Plains', '1;4;'),
(4, 'Primus', 'Plains', '3;1;'),
(5, 'Primus', 'Old Mines', '1;'),
(6, 'Primus', 'Old Mines', '1;');

-- --------------------------------------------------------

--
-- Структура таблицы `plants`
--

CREATE TABLE IF NOT EXISTS `plants` (
  `name` text NOT NULL,
  `growTime` date NOT NULL,
  `locationId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `skills`
--

CREATE TABLE IF NOT EXISTS `skills` (
  `entityId` int(11) NOT NULL,
  `name` text NOT NULL,
  `sValue` int(11) NOT NULL,
  `mValue` int(11) NOT NULL,
  `hardness` float NOT NULL,
  `primaryStat` text NOT NULL,
  `secondaryStat` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `skills`
--

INSERT INTO `skills` (`entityId`, `name`, `sValue`, `mValue`, `hardness`, `primaryStat`, `secondaryStat`) VALUES
(1, 'Herding', 1, 50, 5, 'Int', 'Strength'),
(1, 'Lumberjacking', 0, 50, 5, 'Strength', 'Dexterity'),
(1, 'Chivalry', 0, 50, 5, 'Strength', 'Int'),
(1, 'Parrying', 5, 50, 5, 'Dexterity', 'Strength'),
(1, 'Swords', 50, 50, 5, 'Strength', 'Dexterity'),
(1, 'Mining', 0, 50, 5, 'Strength', 'Int'),
(1, 'Magery', 0, 50, 5, 'Int', 'Int'),
(2, 'Herding', 0, 50, 5, 'Int', 'Strength'),
(2, 'Lumberjacking', 0, 50, 5, 'Strength', 'Dexterity'),
(2, 'Chivalry', 0, 50, 5, 'Strength', 'Int'),
(2, 'Necromancy', 0, 50, 5, 'Int', 'Int'),
(2, 'Swords', 0, 50, 5, 'Strength', 'Dexterity'),
(2, 'Mining', 0, 50, 5, 'Strength', 'Int'),
(2, 'Magery', 0, 50, 5, 'Int', 'Int'),
(3, 'Herding', 0, 50, 5, 'Int', 'Strength'),
(3, 'Lumberjacking', 0, 50, 5, 'Strength', 'Dexterity'),
(3, 'Chivalry', 0, 50, 5, 'Strength', 'Int'),
(3, 'Parrying', 6, 50, 5, 'Dexterity', 'Strength'),
(3, 'Swords', 45, 50, 5, 'Strength', 'Dexterity'),
(3, 'Mining', 0, 50, 5, 'Strength', 'Int'),
(3, 'Magery', 0, 50, 5, 'Int', 'Int');

-- --------------------------------------------------------

--
-- Структура таблицы `stats`
--

CREATE TABLE IF NOT EXISTS `stats` (
  `entityId` int(11) NOT NULL,
  `sValue` int(11) NOT NULL,
  `mValue` int(11) NOT NULL,
  `name` text NOT NULL,
  `hardness` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `stats`
--

INSERT INTO `stats` (`entityId`, `sValue`, `mValue`, `name`, `hardness`) VALUES
(1, 5, 50, 'Strength', 5),
(1, 5, 50, 'Dexterity', 5),
(1, 3, 50, 'Hits', 5),
(1, 5, 50, 'Int', 5),
(2, 5, 50, 'Strength', 5),
(2, 5, 50, 'Dexterity', 5),
(2, 10, 50, 'Hits', 5),
(2, 5, 50, 'Int', 5),
(1, 0, 20, 'Mana', 3),
(3, 5, 50, 'Strength', 5),
(3, 5, 50, 'Dexterity', 5),
(3, 15, 50, 'Hits', 5),
(3, 20, 20, 'Mana', 5),
(3, 5, 50, 'Int', 5);

-- --------------------------------------------------------

--
-- Структура таблицы `worlds`
--

CREATE TABLE IF NOT EXISTS `worlds` (
  `name` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `worlds`
--

INSERT INTO `worlds` (`name`) VALUES
('Primus'),
('Hell');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
