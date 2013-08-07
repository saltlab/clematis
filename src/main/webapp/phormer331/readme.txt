This program is a free software; you can redistribute it and/or modify it under 
the terms of the GNU General Public License as published by the Free Software 
Foundation; either version 2.0 of the License, or (at your option) any later 
version.
 
This program is distributed in the hope that it will be useful, but WITHOUT ANY 
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You can receive a copy of GNU General Public License at the World Wide Web 
address <http://www.gnu.org/licenses/gpl.html>.

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 Product Name		Phormer
 Product URL		http://p.horm.org/er
 Product Description	A PHP (without MySQL) PhotoGallery Manager
 
 Latest Version		3.31
 Release Date 		2007/01/13

 Author's Name		Aidin NasiriShargh 
 Author's Email		aideen[at]gmail[dot]com
 Author's URL		http://i.horm.org
 License		GNU's Public License (GPL)

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

INSTALLATION HOW-TO:
====================
 0 - Download the latest version of phormer###.zip from http://p.horm.org/er
 1 - Unzip the phormer.zip file (~90KB) to the destination folder on your 
     server (e.g. /var/www/photos). (i.e. type this command: "unzip phormer###"
     in the shell).
 2 - Load that by a web browser (e.g. http://somewhere/photos) and fill the 
     short form of preferences.
 That's all!

Note that the base directory of phormer must be writable by php server, for 
the first time installation. thus, "chmod 777" this base directory (where 
index.php, admin.php & etc are placed) after the unzip, and when installation 
completed, turn them back to 711. briefly, after uploading, the following 
commands shall be entered:
   $>unzip phormer###
   $>chmod 777 . 
and after installation (getting the `installation completed' message):
   $>chmod 711 .

   
UPDATE HOW-TO:
==============
 0 - Remove folder "files/" and all files in root of installation directory 
     (e.g. index.php, admin.php and etc.) after that, there should be just 3 
     folders of "data/", "images/" and "temp/" there with no files in root 
     folder.
     HINT: in correct accessing mode, those 3 folders are owned by PHP and you
     may not delete them.
 1 - place all the contents of download phormer (i.e. a folder of "files/" and 
     some files to be placed at root of installation) there. no conflict should
     occur.
 2 - go to administration area (http://somewhere/photos/admin.php) and work 
     fine! All your images would be saved through an update.
 That's all!

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

FILES ARCHITECTURE:
===================
  /{Installation Directory}
   |
   +- data/ 			# Keeps a set of .xml files like a database
   |   |
   |   +- adminPass.php		#    MD5-ed version of Administration Password
   |   +- basis.xml		#    Basic configurations like theme
   |   +- basis.xml.bku		#    A backup of last saved basis.xml
   |   +- ...
   |   +- p_00000i.xml		#    Data of Photo #i
   |
   +- files/			# Phormer's own files, like css
   |   |
   |   +- adminfiles/		#    Administration area's files
   |   |   |			
   |   |   +- addphoto.js	#        Javascript of adding photos
   |   |   +- admin.css		#        CSS of admin.php
   |   |   +- photos.xml.def	#        Default XML file of data/photos.xml
   |   |   +- ...
   |   |   +- help.js		#        Help messages
   |   |   +- skeleton.js	#        Thumbnail skeleton picker js
   |   |
   |   +- cssfiles/		#    Images of different themes
   |   |   |
   |   |   +- black_hbg0.jpg
   |   |   +- ...
   |   |
   |   +- externalcss/		#    Additional External CSS Files
   |   |
   |   +- phorm.js		#    JavaScript file of index.php
   |
   +- images/
   |   |
   |   +- 00000i_j.jpg		#    j-th thumbnail of photo #i
   |   +- ...
   |
   +- temp/			# Temporary/Draft photos/files which ain't public
   |
   +- admin.php			# Administration area 
   |
   +- css.php			# CSS file of index.php, generates upon theme
   |
   +- funcs.php			# Functions of admin.php and index.php
   |
   +- index.php			# Viewable file for public
   |
   +- index.xml			# RSS feed file, auto-generated
   |
   +- readme.txt		# This file!
   |
   +- upload.php		# PHP files of uploading photos (AJAX)
   |
   +- wv.php			# Word Verification PHP

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

FEATURES:
=========

* Easy webbased setup: 	Just copy the files and then enter admin's password! 
			Also uninstallation is so easy and can be done from
			inside the Phormer by one click (after confirmation!).
* Fast and valid pages:	Tableless and human-made XHTML codes for both admin
			and visitors page, leads faster and cleaner pages which
			are tested in common browers! also the image-less and
			comfortable administration region has provided very 
			fast and exact navigation whole around!
* XML databases: 	Light, easy to handle/manage/backup/manualModification.
			Doesn't require any sql (mysql, pgsql) account on 
			machine, just a php server!
* Category/Story: 	Having two separate categorization method will lead to
			handle everything in the most right place! -- You can 
			both categorize your shots by Category (e.g. black and 
			white, self-portrait, etc.) and, on the other hand, by 
			date (which named story), just like the posts of a 
			weblog.
* Hierarchical Categs:	Each photo can be placed in several categories/stories. 
			also the Categories/Stories, themselves can be 
			inherited.
* Improved Privacy:	Leads both private (passworded of each own) and 
			unlisted (unseen from the first page) mode for the 
			Categories/Stories.
* Link Manager:		Manage the side-bar links of your blogs, visually from 
			inside the administration area, so fast and clean!
* File gathering mods:	Image files can be acquired either from the PC (by 
			Ajaxian upload manager) or from somewhere across the 
			web (just enter url!).
* Ajax Upload Manager:	Name and fill information of your shot while its upload 
			progress goes (ajaxly) on background!
* Square thumbs:	Easy WYSIWYG Part-of-Image Square-Thumb maker tool 
			helps you to create the best square thumb from your 
			shots which is best-fitted and much more catchy! 
			various sizes of whole image can also be generated, 
			beside.
* Editable add-shot:	Edit and regenerate/re-enter everything at everytime, 
			even the photo's source file!
* Advanced Commenting:	Manage them easily. Control visitors to leave comments 
			on each photo/story or not.
* RSS Feed:	  	Don't Lose RSS-Addicted visitors! index.xml for last
			photo entries will be generated after each photo 
			add/del.
* Various Themes: 	You'll never get bored with 10 different styles.
* Various styles: 	3 different styles for index page: photos, stories, 
			jungleBox (where all the shots can be place above each 
			other and be highlighted when pointed!). a Random of
			them can also be selected!
* Rating system:	Save the opinion (rating) of visitors about each single 
			photo and sort them in that way!
			Note: on version 2.30 and later, the Rating is moved on 
			AJAX system and visitors doesn't need to load the page 
			once again!
* Comment Manager:	Save each Photo/Story's comments separately which can 
			be disabled (for each item) in admin's page. also 
			catches the date and the IP of each comment. they can 
			be browsed totally sorted-by-date by admin and be 
			deleted (if needed!) by a single click.
			also, since version 3.10, word verification feature has
			comen to help fighting against spam commenters!
* Hit Counter:		Counts each shot's view-times, separately!
* Easy export:		Use each photo in your weblog/website (in proper size) 
			by a single code (e.g. something like this line:
			<script src="http://mywebby.com/?j=290"></script>) in 
			various sizes.
			Also, since version 3.30, export method in PHP is 
			available - just put this line in your PHP code:
			include("http://mywebby.com/?jj=290");
			Note that in both modes of export, you can set pass a
			size parameter (in range [0..4] to get various sizes)
			like: include("http://mywebby.com/?jj=290&size=2");
			in PHP export!			
* Info gatherer (EXIF):	Save the photo info (like focus mode, shutter speed, 
			...), date taken and every other precise data about 
			each shot. Since version 3.20, EXIF handling is also 
			enabled.
* I18n: 		using unicode (UTF-8) as whole encoding system, has 
			provided a reliable support of various languages.

  ~ ~ ~
  
ALSO, ON VERSION 3.30 AND AFTER:
* Powerful Backup	Anytime, each of your essential XML files missed or 
			corrupted Due to a server lack of write or synchronous 
			problem, Phormer will automatically notify you of that 
			and helps you to restore the last saved backup. 
                        NOTE: always log-out to save a back-up of your files, 
                        automatically!
* Help			Interactive help on every menu feeds you the required 
                        informations about each item.
* Automatic Update	Your PG, by contacting to the homepage of Phormer, will
			notify of that!
* Statistics		Online visitors count, today, yesterday & this month
			visitors count.
* Mass Upload		Now, you can upload many of your files into a .zip file
			and also add several inside another local directory!
* External CSS		Is now supported, just put them into files/externalcss
			and pick them inside the preferences area.
* SlideShow		Is available now! both for stories and categories and
			also for recent photos
* New recenting modes!	Recently commented and Random photos are also added to
			First page indexing made, which is rebuilt.

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This Readme is Last-Updated on 2007/01/13 by Aidin NasiriShargh [http://i.horm.org].