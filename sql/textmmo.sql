-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Хост: 127.0.0.1
-- Время создания: Июн 27 2014 г., 10:24
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
('Alastar2', '1', 'some2@mail.ru', 2);

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
('Durability', 3, -69);

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Дамп данных таблицы `entities`
--

INSERT INTO `entities` (`id`, `caption`, `type`, `locationId`, `ai`) VALUES
(1, 'Alastar', 'Human', 3, ''),
(2, 'Zuzya', 'Elf', 1, '');

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
(2, 20);

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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Дамп данных таблицы `items`
--

INSERT INTO `items` (`id`, `locationId`, `caption`, `amount`, `entityId`, `type`, `actionType`) VALUES
(1, 1, 'Coin', 1, 1, 'None', 'None'),
(2, 1, 'Coin', 1, 2, 'None', 'None'),
(3, 1, 'Wooden axe', 1, 1, 'None', 'Cut');

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
(4, 'Wood', '');

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
(1, 'Primus', 'River', '4;3'),
(2, 'Hell', 'Lava Lake', ''),
(3, 'Primus', 'Plains', '1;4'),
(4, 'Primus', 'Plains', '3;1');

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
(1, 'Taming', 0, 50, 5, 'Int', 'Strength'),
(1, 'Lumberjacking', 0, 50, 5, 'Strength', 'Dexterity'),
(1, 'Chivalry', 0, 50, 5, 'Strength', 'Int'),
(1, 'Necromancy', 0, 50, 5, 'Int', 'Int'),
(1, 'Swords', 0, 50, 5, 'Strength', 'Dexterity'),
(1, 'Mining', 0, 50, 5, 'Strength', 'Int'),
(1, 'Magery', 0, 50, 5, 'Int', 'Int'),
(2, 'Taming', 0, 50, 5, 'Int', 'Strength'),
(2, 'Lumberjacking', 0, 50, 5, 'Strength', 'Dexterity'),
(2, 'Chivalry', 0, 50, 5, 'Strength', 'Int'),
(2, 'Necromancy', 0, 50, 5, 'Int', 'Int'),
(2, 'Swords', 0, 50, 5, 'Strength', 'Dexterity'),
(2, 'Mining', 0, 50, 5, 'Strength', 'Int'),
(2, 'Magery', 0, 50, 5, 'Int', 'Int');

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
(1, 14, 50, 'Hits', 5),
(1, 5, 50, 'Int', 5),
(2, 5, 50, 'Strength', 5),
(2, 5, 50, 'Dexterity', 5),
(2, 10, 50, 'Hits', 5),
(2, 5, 50, 'Int', 5);

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
