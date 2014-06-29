-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Хост: 127.0.0.1
-- Время создания: Июн 29 2014 г., 18:22
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
('Alastar', '��B8��#�\r�P�ou��', 'some@mail.ru', 4),
('Alastar2', '��B8��#��P�ou��', '', 1),
('Alastar2', '��B8��#�\r�P�ou��', 'mail@mail.ru', 5);

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
('Durability', 6, 949),
('Durability', 2, 893),
('Charges', 17, 10);

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

--
-- Дамп данных таблицы `entities`
--

INSERT INTO `entities` (`id`, `caption`, `type`, `locationId`, `ai`) VALUES
(4, 'Wookie', 'Elf', 6, ''),
(5, 'Zuzya', 'Dragon', 1, '');

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
(4, 20),
(5, 20);

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=19 ;

--
-- Дамп данных таблицы `items`
--

INSERT INTO `items` (`id`, `locationId`, `caption`, `amount`, `entityId`, `type`, `actionType`) VALUES
(2, 1, 'Wooden pickaxe', 1, 4, 'None', 'Mine'),
(6, 3, 'Wooden axe', 1, 4, 'None', 'Cut'),
(7, 5, 'wheat', 3, 4, 'None', 'None'),
(8, 5, 'emerald', 7, 4, 'None', 'None'),
(9, 5, 'iron ore', 9, 4, 'None', 'None'),
(10, 5, 'ginseng', 7, 4, 'None', 'None'),
(11, 5, 'swiftstone', 3, 4, 'None', 'None'),
(12, 5, 'copper ore', 7, 4, 'None', 'None'),
(14, 1, 'Coin', 1, 4, 'None', 'None'),
(15, 1, 'Coin', 1, 5, 'None', 'None'),
(17, 6, 'Wooden Totem', 10, 4, 'None', 'Cast');

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
('heal', 4);

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

--
-- Дамп данных таблицы `plants`
--

INSERT INTO `plants` (`name`, `growTime`, `locationId`) VALUES
('wheat', '2014-06-30', 4);

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
(4, 'Carpentry', 50, 50, 5, 'Int', 'Strength'),
(4, 'Lumberjacking', 2, 50, 5, 'Strength', 'Dexterity'),
(4, 'Herding', 0, 50, 5, 'Int', 'Int'),
(4, 'Swords', 32, 50, 5, 'Strength', 'Dexterity'),
(4, 'Necromancy', 0, 50, 5, 'Int', 'Int'),
(4, 'Magery', 0, 50, 5, 'Int', 'Int'),
(4, 'Chivalry', 0, 50, 5, 'Strength', 'Int'),
(4, 'Parrying', 3, 50, 5, 'Dexterity', 'Strength'),
(4, 'Mining', 0, 50, 5, 'Strength', 'Int'),
(5, 'Taming', 0, 50, 5, 'Int', 'Strength'),
(5, 'Lumberjacking', 0, 50, 5, 'Strength', 'Dexterity'),
(5, 'Herding', 0, 50, 5, 'Int', 'Int'),
(5, 'Swords', 33, 50, 5, 'Strength', 'Dexterity'),
(5, 'Necromancy', 0, 50, 5, 'Int', 'Int'),
(5, 'Magery', 0, 50, 5, 'Int', 'Int'),
(5, 'Chivalry', 0, 50, 5, 'Strength', 'Int'),
(5, 'Parrying', 6, 50, 5, 'Dexterity', 'Strength'),
(5, 'Mining', 0, 50, 5, 'Strength', 'Int');

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
(4, 5, 50, 'Strength', 5),
(4, 5, 50, 'Dexterity', 5),
(4, 20, 50, 'Hits', 5),
(4, 0, 20, 'Mana', 5),
(4, 5, 50, 'Int', 5),
(5, 5, 50, 'Strength', 5),
(5, 5, 50, 'Dexterity', 5),
(5, 3, 50, 'Hits', 5),
(5, 20, 20, 'Mana', 5),
(5, 5, 50, 'Int', 5);

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
