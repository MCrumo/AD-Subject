-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 15-12-2023 a las 19:42:45
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `pr2`
--
CREATE DATABASE IF NOT EXISTS `pr2` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `pr2`;

DELIMITER $$
--
-- Procedimientos
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteImage` (IN `i_id` INT)   BEGIN
    SELECT filename FROM PR2.IMAGE WHERE ID = i_id;
    DELETE FROM PR2.IMAGE WHERE ID = i_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllImages` ()   BEGIN
	SELECT * FROM PR2.IMAGE ORDER BY STORAGE_DATE DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getImageById` (IN `i_id` INT)   BEGIN
	SELECT * FROM PR2.IMAGE WHERE ID = i_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getImageFilename` (IN `i_id` INT)   BEGIN
	SELECT FILENAME FROM PR2.IMAGE WHERE ID = i_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getImagesByAuthor` (IN `i_author` VARCHAR(256))   BEGIN
	SELECT * FROM PR2.image WHERE AUTHOR LIKE CONCAT('%', i_author, '%') ORDER BY STORAGE_DATE DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getImagesByCoincidence` (IN `i_coincidence` VARCHAR(256))   BEGIN
	SELECT * FROM PR2.image WHERE 
    TITLE LIKE CONCAT('%', i_coincidence, '%') OR 
    DESCRIPTION LIKE CONCAT('%', i_coincidence, '%') OR 
    AUTHOR LIKE CONCAT('%', i_coincidence, '%') OR 
    CAPTURE_DATE LIKE CONCAT('%', i_coincidence, '%') OR 
    KEYWORDS LIKE CONCAT('%', i_coincidence, '%') 
    ORDER BY STORAGE_DATE DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getImagesByCreationDate` (IN `i_creation_date` VARCHAR(10))   BEGIN
	SELECT * FROM PR2.image WHERE CAPTURE_DATE LIKE i_creation_date ORDER BY STORAGE_DATE DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getImagesByKeyword` (IN `i_keyword` VARCHAR(256))   BEGIN
	SELECT * FROM PR2.image WHERE KEYWORDS LIKE CONCAT('%', i_keyword, '%') ORDER BY STORAGE_DATE DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getImagesByTitle` (IN `i_title` VARCHAR(256))   BEGIN
	SELECT * FROM PR2.image WHERE TITLE LIKE CONCAT('%', i_title, '%') ORDER BY STORAGE_DATE DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserById` (IN `u_id` VARCHAR(256))   BEGIN
	SELECT * FROM PR2.usuarios WHERE id_usuario = u_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `insertImage` (IN `i_title` VARCHAR(256), IN `i_description` VARCHAR(1024), IN `i_keywords` VARCHAR(256), IN `i_author` VARCHAR(256), IN `i_creator` VARCHAR(256), IN `i_capture_date` VARCHAR(10), IN `i_storage_date` VARCHAR(10), IN `i_filename` VARCHAR(512))   BEGIN
    INSERT INTO PR2.IMAGE (TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME)
    VALUES (i_title, i_description, i_keywords, i_author, i_creator, i_capture_date, i_storage_date, i_filename);
    SELECT LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `insertUser` (IN `id_usr` VARCHAR(256), IN `password` VARCHAR(256))   BEGIN
    INSERT INTO PR2.USUARIOS (ID_USUARIO, PASSWORD)
    VALUES (id_usr, password);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateFilename` (IN `i_id` INT, IN `i_filename` VARCHAR(512))   BEGIN
	UPDATE PR2.IMAGE SET filename = i_filename WHERE ID = i_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateImage` (IN `i_id` INT, IN `i_title` VARCHAR(256), IN `i_description` VARCHAR(1024), IN `i_keywords` VARCHAR(256), IN `i_author` VARCHAR(256), IN `i_capture_date` VARCHAR(10), IN `i_filename` VARCHAR(256))   BEGIN
	UPDATE PR2.IMAGE SET TITLE = i_title, DESCRIPTION = i_description, KEYWORDS = i_keywords, AUTHOR = i_author, CAPTURE_DATE = i_capture_date, FILENAME = i_filename WHERE ID = i_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `validUser` (IN `u_username` VARCHAR(256), IN `u_password` VARCHAR(256), OUT `u_exists` INT)   BEGIN
    DECLARE v_count INT;

    -- Buscar el usuario en la tabla
    SELECT COUNT(*) INTO v_count
    FROM PR2.USUARIOS
    WHERE ID_USUARIO = u_username AND PASSWORD = u_password;

    -- Asignar el resultado al parámetro de salida
    SET u_exists = IF(v_count > 0, 1, 0);
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `image`
--

CREATE TABLE `image` (
  `id` int(11) NOT NULL,
  `title` varchar(256) NOT NULL,
  `description` varchar(1024) NOT NULL,
  `keywords` varchar(256) NOT NULL,
  `author` varchar(256) NOT NULL,
  `creator` varchar(256) NOT NULL,
  `capture_date` varchar(10) NOT NULL,
  `storage_date` varchar(10) NOT NULL,
  `filename` varchar(512) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `image`
--

INSERT INTO `image` (`id`, `title`, `description`, `keywords`, `author`, `creator`, `capture_date`, `storage_date`, `filename`) VALUES
(1, 'gat-monu', 'un gat molt bonic', 'gat,curios,precios,bonic', 'silvia', 'silvia', '2023-12-15', '2023-12-15', '1_gat-monu.jpeg'),
(2, 'Amelia', 'novament un gat molt maco', 'gat,assegut,precios,bonic,xulo', 'marcos', 'silvia', '2023-12-10', '2023-12-15', '2_Amelia.png'),
(3, 'gruñón', 'mi gato es muy gruñón', 'gato,gruñón,precios,bonito', 'ofelia', 'ofelia', '2023-12-10', '2023-12-15', '3_gruñón.jpg'),
(4, 'persa', 'us presento a un gat persa', 'gat,maco,precios,persa', 'pepito', 'pepito', '2023-12-11', '2023-12-15', '4_persa.jpg'),
(5, 'suplicant', 'el gat de la meva avia suplicant', 'gat,maco,bonic,suplicant', 'avia', 'pepito', '2023-12-10', '2023-12-15', '5_suplicant.gif'),
(6, 'caiguda', 's\'ha desequilibrat!', 'gat,gatet,bonic,equilibri', 'artur', 'ofelia', '2023-12-11', '2023-12-15', '6_caiguda.gif');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id_usuario` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `password`) VALUES
('lolito', '$2b$05$sfZWjlsNcjFuLSsYdaQzUOPP5T3kWHFuLVfTFkBsb6aORSyFFp.kC'),
('ofelia', '$2a$05$pUqu6JLBN3ycSrrTyJrqVuRruZOWhAu3WaAf0.yiF6Z8sQFjX.3X6'),
('pepito', '$2a$05$gNSCcRI3IlnynQSFat6euOqd4SiNQh4OK4wg5RwT9i9qE7cMTpaMW'),
('silvia', '$2a$05$/sIMi9k7DVCoIimu8WPhO.WhQ0bxFPFkPa5uvCfY0lWQrtTc7XgrS');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `image`
--
ALTER TABLE `image`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_usuario` (`creator`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id_usuario`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `image`
--
ALTER TABLE `image`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `image`
--
ALTER TABLE `image`
  ADD CONSTRAINT `id_usuario` FOREIGN KEY (`creator`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
