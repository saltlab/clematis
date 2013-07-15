/** @preserve jquery.jqdock.js v2.0.2, by Wizzud
 */
/**
 * jqdock jQuery plugin
 * Version : 2.0.2
 * Author : Roger Barrett
 * Date : Dec 2011
 *
 * Inspired by:
 *   iconDock jQuery plugin
 *   http://icon.cat/software/iconDock
 *   version: 0.8 beta
 *   date: 2/05/2007
 *   Copyright (c) 2007 Isaac Roca & icon.cat (iroca@icon.cat)
 *   Dual licensed under the MIT-LICENSE.txt and GPL-LICENSE.txt
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 * Dual licensed under the MIT-LICENSE.txt and GPL-LICENSE.txt
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 *
 * Change Log :
 * v2.0.2
 *    - added permanentLabels switch option (off by default) which keeps the labels (if enabled) on permanent display
 *    - fixed for jQuery v1.9, which no longer supports $.browser
 * v2.0.1
 *    - bugfix : the noAntiFlutter option (introduced in v2.0.0) was being applied in reverse! ie. you had to set noAntiFlutter ON in order to run the anti-flutter code! Now fixed to work as documented/intended!
 * v2.0.0
 *    - IMPORTANT! : Requires jQuery v1.7+
 *    - versioning changed to major.minor.patch - with this being a major release - in order to meet jQuery's new requirements for plugins
 *    - removed support for $.metadata
 *    - added support for (namespaced) HTML5 data-jqdock-[option name] attributes, eg. data-jqdock-align='middle', data-jqdock-fade-in=800, etc (note conversion of camelback option names to hyphenated names!)
 *    - added support for data-jqdock-altsrc='large image path' attribute on any image, as an alternative to (and higher priority than) using the alt attribute
 *    - added provision for a 'jqDockAuto' class on the menu element to automatically run jqDock() on document ready
 *    - new option, sizeMax (default 0), which does the same as the size option but for the fully expanded image, ie. sets the minor axis dimension for full expansion
 *    - new option, capSizing (default Off), which changes the 'at rest' (size option) and fully expanded (sizeMax option) minor axis dimension calculations from absolute to capped (ie. only applied if the dimension in question is greater than the target value set by the option)
 *    - new option, noAntiFlutter (default Off), allows the anti-flutter (anti-oscillation) code to be disabled/bypassed
 *    - new custom event, dockitem, which is triggered on the original menu element whenever an item leaves or returns to the 'at rest' position; the event is passed 2 extra parameters : the related image DOM element, and a boolean (true=expanded, false=at rest)
 *    - added the ability to 'convert' UL & OL lists; $().jqdock() will do it automatically (returning the conversion in the chain in place of the original), or $.jqdock.convert(list element) will return the conversion element; destroying a conversion will revert the list
 *    - while any item is expanded (ie. not 'at rest') to whatever degree, the .jqDockItem wrapper element now gets an additional class of 'jqDockExpand'
 *    - shifted the internal storage of the Dock's working variables into the menu element's data() (as data('JQDOCK'))
 *    - some tweaks and re-structuring to reduce the compressed footprint (and keep JSLint happy ... again!)
 * v1.9
 *    - bugfix : in Chrome, jquery v1.2.x appears not to hide() elements that are not yet attached to the DOM
 *    - allowed for a boolean false to be specified for the onReady, onSleep and onWake options, that then provides a function that always and only returns false
 *    - added handling for an HTML5 data-jqdock attribute (as an object) on the menu element, eg. data-jqdock='{"align":"top","size":80,"onReady":false}'; NB: requires jQuery v1.4.3 or later!
 *    - made jqDock caseless, eg. $.jqdock == $.jqDock, and $.fn.jqdock() == $.fn.jqDock(), so either can be used
 *    - changed some for(...) loops into while(...) loops to keep JSLint happy
 * v1.8
 *    - bugfix : both v1.5 & v1.7 tried to fix the problem of clicking on labels, first by adding return false; to the LABEL_CLICK handler, then by removing it; this time I've switched the trigger() for triggerHandler(), to prevent the event bubbling up the DOM
 *    - move the assignment of a label's click handler such that it is now bound regardless of whether labels are enabled
 * v1.7
 *    - bugfix : remove the 'return false;' from the LABEL_CLICK handler because it prevents label clicks being notified to the parent anchor (if present)
 *    - new option, bias (default 50) which is the percentage expansion (range 0-100) on the *leading* edge side, for middle and center aligned expansion
 *    - some minor syntax changes to keep JSLint happy
 * v1.6
 *    - bugfix : when initially fading in, the 'Asleep' state wasn't being cleared (typo) which meant that the Dock would run onWake(), and possibly trigger dockwake, which it shouldn't
 *    - new option, active (default -1), which is the index (zero-based) of an image required to be expanded on initial display
 *    - new option, noBuffer (default false), which disables the buffering of the last mouse event while the dock is asleep
 *    - added a 'destroy' command to jqDock() function - jqDock('destroy') - which removes jqDock from a menu
 *    - added 'expand' and 'active' commands, which expand a selected image to full size, with/without animation respectively; NB dock gets frozen!
 *    - expanded the (previously undocumented) 'get' command to return either the internal Dock object, or an internal image object, depending on the selector (v1.5 'get' only handled images)
 *    - added listener for custom event - dockfreeze - on the original menu element (as a sub-function of docksleep), which can be triggered by the calling program to (try to) freeze the dock, ie. put it to sleep but without 'tidying up' first
 *    - added a 'freeze' command, which does the same thing as triggering the new dockfreeze event (but synchronously)
 *    - the onReady, onSleep and onWake hook functions are now each passed a single argument - 'ready', 'sleep' or 'freeze', and 'wake' or 'thaw' respectively
 *    - the triggered custom events - dockshow, docksleep and dockwake - are now passed 1 extra parameter when being triggered - 'ready', 'sleep' or 'freeze', and 'wake' or 'thaw' respectively
 *    - the setup of labels has changed slightly...
 *      - the outer label container (div.jqDockLabel) now gets created, styled, and has its click handler bound, *before* setLabel() is called
 *      - setLabel() gets passed an extra parameter: the DOM element, div.jqDockLabel
 *      - setLabel can now return false to prevent jqDock doing anything further with the label; otherwise it is expected
 *        to return an html string as before, which jqDock will create an inner container for - div.jqDockLabelText - and append to the outer container
 *    - the decision of whether or not to 'show' labels is now solely dependent on the 'labels' option setting
 *    - partial expansion/collapse times (on mouseenter/leave) are now equal - eg. going on then off the menu (before expansion has completed) will allocate the same time to the collapse as was used for the expansion (instead of always using the full 'duration')
 *    - re-worked the timings and added a 'tidy-up' loop - previous versions were too dependent on receiving mousemove events to complete the animations (particularly noticeable with 'flow' enabled)
 * v1.5
 *    - bugfix : the label click handler was not returning false, so clicks on labels were being notified to links (not images) twice
 *    - new option, setLabel (default false), as a function called when initialising the label contents for each menu item
 *    - added an extra layer - div.jqDockLabelText - inside div.jqDockLabel to facilitate positional 'tweaking' of the label without having to resort to the setLabel option
 *    - new option, flow (default false), allowing the auto-centering to be disabled and the dock wrapper element to auto-size to precisely contain the dock
 *    - new option, idle (default 0), as the number of milliseconds of idle time after the mouse has left the menu before the dock goes to sleep and the docksleep event is triggered (on the original menu element)
 *    - new option, onSleep, as a function which is called with scope (this) of the original menu element when an optional number of milliseconds (the idle option) has elapsed since the mouse left the menu; returning false will prevent the dock from going to sleep
 *    - new option, onWake, as a function which is called with scope (this) of the original menu element when dock is 'nudged' awake, but only if dock was asleep at the time; returning false will prevent the dock waking up (stays asleep)
 *    - new option, onReady, as a function which is called with scope (this) of the orginal menu element when dock has been initialised and is ready for display; returning false will prevent the dock being displayed
 *    - new custom event, dockshow, which is triggered on the original menu element when the dock has been completely initialised; this won't be triggered if the onReady() call returns false
 *    - new custom event, docksleep, which is triggered on the original menu element following the onSleep() call, unless the onSleep() call returns false
 *    - new custom event, dockwake, which is triggered on the original menu element following the onWake() call, unless the onWake() call returns false
 *    - added listener for custom event - docknudge - on the original menu element, which *has* to be triggered by the calling program in order to (try to) wake the dock from a sleep
 *    - added listener for custom event - dockidle - on the original menu element, which can be triggered by the calling program to (try to) put the dock to sleep
 *    - added 2 commands to jqDock() function - jqDock('nudge') and jqDock('idle') - which do the same thing as triggering the respective docknudge and dockidle events (but synchronously)
 *    - jqDock no longer hides the original menu element, since most likely usage is to pre-hide it to prevent 'flicker'; also now copes with visibility:hidden (as well as display:none)
 *    - labels no longer get jqDockMouseN class
 * v1.4
 *    - bugfix : in IE8, non-statically positioned child elements do not inherit opacity, so fadeIn did not work correctly
 *    - new option, fadeLayer (default ''), allows the fade-in to be switched from the original menu element down to either the
 *      div.jqDockWrap or div.jqDock layer
 * v1.3
 *    - new option, inactivity (default 0), allowing auto-collapse after a specified period (mouse on dock)
 *    - new option, fadeIn (default 0), allowing initialised menu to be faded in over a specified period (as opposed to an instant show)
 *    - new option, step (default 50), which is the interval between animation steps
 *    - default size increased to 48 (from 36)
 *    - default distance increased to 72 (from 54)
 *    - default duration reduced to 300 ms (from 500 ms)
 *    - better 'best guess' for maximum dimensions of Dock
 *    - handle integer options being passed in as strings (eg. size:'48' instead of size:48)
 *    - the wrapper div now has width, height, and a class
 *    - all menu items are double-wrapped now in 2 divs 
 *    - double-wrap resolves ie8 horizontal float problem
 *    - dimensioning switched from image to innermost of the item's double-wrap
 *    - labels now assigned per menu item instead of one for the entire dock
 *    - labels within anchors so clicking activates anchor
 *    - labels are always created, regardless of option setting
 *    - default label position changed from 'tc' to 'tl' for any alignment except 'top' (labels='br') and 'left' (labels='tr')
 *    - events switched from mouseover/out to mouseenter/leave
 * v1.2
 *    - Fixes for Opera v9.5 - many thanks to Rubel Mujica
 * v1.1
 *    - some speed optimisation within the functions called by the event handler
 *    - added positioning of labels (top/middle/bottom and left/center/right)
 *    - added click handler to label (triggers click event on related image)
 *    - added jqDockLabel(Link|Image) class to label, depending on type of current image
 *    - updated demo and documentation for label positioning and clicking on labels
 */
//options for JSLint (http://www.jslint.com/)...
/*global jQuery, window:false, Image:false*/
/*jslint white:true, plusplus:true, sloppy:true, forin:true, unparam:true*/
(function($, window){
if(!$.jqDock){ //can't see why it should be, but it doesn't hurt to check
	var ONE = 1, //cheat to get past JSLint objecting to things like var i_want_a_number = 1 * var_of_unknown_type;
			TRBL = ['Top', 'Right', 'Bottom', 'Left'],
			AXES = ['Major', 'Minor'],
			CLASSES = [],
			DOTCLASS = $.map(['ed', 'Item', 'Mouse', 'Label', 'Filter', 'Expand', 'Wrap', 'List', ''], function(v, i){
				CLASSES[i] = 'jqDock' + v;
				return '.' + CLASSES[i];
			}),
//ie. CLASSES = ['jqDocked', 'jqDockItem', 'jqDockMouse', 'jqDockLabel', 'jqDockFilter', 'jqDockExpand', 'jqDockWrap', 'jqDockList', 'jqDock],
//and DOTCLASS is the same but with all elements prefixed with a '.'
			DATA_NAMES = ['JQDOCK', 'JQDOCK_conv', 'JQDOCK_li', 'JQDOCK_list'],
			MOUSEEVENTS = ['mouseenter','mousemove','mouseleave'],
			CUSTOMEVENTS = ['docknudge','dockidle','dockfreeze'],
			TIMERS = ['Idler','Inactive','Indock','Overdock','Offdock'],
			VERTHORZ = { //note : lead and trail are indexes into TRBL
				v: { wh:'height', xy:1, tl:'top', lead:0, trail:2, inv:'h' }, //Opts.align = left/center/right
				h: { wh:'width', xy:0, tl:'left', lead:3, trail:1, inv:'v' } //Opts.align = top/middle/bottom
			},
			XY = [0, 0], //mouse position from left, mouse position from top
			WIN_TIMEOUT = window.setTimeout,
/** returns 'JQDOCK' data for a jQuery'd element
 * @private
 * @param {object} x A jQuery object
 * @return {object} An object or undefined
 */
			DATA = function(x){
				return x.data(DATA_NAMES[0]);
			},
/** returns a Boolean false
 * @private
 * @return {boolean} Always FALSE
 */
			FALSEFUNC = function(){
				return false;
			},
/** gets the index of the passed-in item within .jqDockItem siblings
 * @private
 * @param {element|object} el Element or jQuery object
 * @return {integer} Index or -1 if not found
 */
			ITEM_INDEX = function(el){
				return $(el).parent().children(DOTCLASS[1]).index(el);
			},
/** returns an object containing width and height, with one value being 'dim' and the other
 *  being calculated proportionately from 'dim', with the verthorz object indicating whether
 *  'dim' is width or height
 *  if horizontal menu then attenuation is along horizontal (x) axis, thereby setting the new
 *  dimension for width, so the one to keep in proportion is height; and vice versa for
 *  vertical menus, obviously!
 * @private
 * @param {object} el Element of Elem array
 * @param {integer} dim Image dimension
 * @param {object} verthorz One of VERTHORZ's objects, ie. either [v]ertical or [h]orizontal
 * @param {boolean} cap Switch indicating that dimensions are capped to a maximum as opposed to being set absolutely
 * @return {object} The provided dimension and the proportioned dimension, as width and height (but not necessarily respectively!)
 */
			KEEP_PROPORTION = function(el, dim, verthorz, cap){
				var r = {},
						invwh = VERTHORZ[verthorz.inv].wh, //convenience
						setAbsolute = !cap || el[verthorz.wh] > dim;
				r[verthorz.wh] = setAbsolute ? dim : el[verthorz.wh];
				r[invwh] = setAbsolute ? Math.round(dim * el[invwh] / el[verthorz.wh]) : el[invwh];
				return r;
			},
/** get closest menu (.jqDocked) or item (.jqDockItem) as jQuery object
 * @private
 * @param {element|object} el DOM element or jQuery object
 * @param {integer} item Index into DOTCLASS array
 * @return {object} jQuery object
 */
			MENU_OR_ITEM = function(el, item){
				return $(el).closest(DOTCLASS[item || 0 ]);
			},
/** returns a filtered data() object containing only jqdock namespaced options
 * @param {object} x A data() object
 * @return {object} Filtered object, with jqdockPropertyName:value returned as propertyName:value
 */
			NAMESPACED_DATA = function(x){
				var rtn = {},
						n, m;
				for(n in x){
					m = n.match(/^jqdock([A-Z])([a-zA-Z]+)$/);
					if(m){
						rtn[ m[1].toLowerCase() + m[2] ] = x[n];
					}
				}
				return rtn;
			},
/** returns integer numeric of leading digits in string argument
 * @private
 * @param {string} x String representation of an integer
 * @return {integer} Number
 */
			AS_INTEGER = function(x){
				var r = parseInt(x, 10);
				return isNaN(r) ? 0 : r;
			},
/** clears a specified timeout timer, or all timers if the supplied index is less than zero
 * @private
 * @param {object} Dock Dock object
 * @param {integer} x Index into TIMERS of timer to clear, or a negative number to clear all timers
 */
			CLEAR_TIMER = function(Dock, x){
				var y = TIMERS[x] ? x + 1 : TIMERS.length;
				while(x < y && y--){
					if(Dock[TIMERS[y]]){
						window.clearTimeout(Dock[TIMERS[y]]);
						Dock[TIMERS[y]] = null;
					}
				}
			},
/** creates a DIV menu from a list, with DIV children being the first image (or anchor wrapper thereof) of each list item
 * @private
 * @this {element} DOM element of original menu (a UL or OL list)
 * @return {element} The new DIV, or the conversion (if already converted), or the original element (if it isn't a list)
 */
			CONVERT_LISTS = function(){
				var original = $(this),
						id = original.attr('id') || '',
						converted = original.data(DATA_NAMES[1]),
						convertables = original.children('li').map(function(){
							//map the LI to either the first image or that image's parent anchor (where the image is the sole child)...
							var img = $('img', this).eq(0),
									anchor = img.parent('a');
							return anchor.length ? (img.siblings().length ? null : anchor[0]) : img.get(0);
						}),
						attrs = id ? {id:id} : {},
						rtn;
				//if the list has no convertable list items (may not even be a list!), then cop out, returning
				//the original element (scope)...
				if(!original.length || !convertables.length){
					return this;
				}
				//if the list is already converted then cop out, returning the conversion...
				if(converted){
					return converted;
				}
				//get the original list's style attribute...
				rtn = original.attr('style');
				if(rtn){
					attrs.style = rtn;
				}
				//the styling (display, visibility, position, etc) I'm leaving to the page's css, since
				//it's fairly easy to differentiate between ul#menu and div#menu (if you need to)!
				//but just in case the original doesn't have an id or class that can be used to uniquely
				//identify the created div, I'm going to give the div an extra class of jqDockList, so
				//that can be used if nothing else
				//create div.jqDockList...
				rtn = $('<div/>').addClass(CLASSES[7])
					//...copy jqdock-namespaced data() from the original...
					.data('jqdock', $.extend({}, original.data('jqdock'), NAMESPACED_DATA(original.data())))
					//...also store the original element and some of its attributes (eg. id & style)...
					.data(DATA_NAMES[3], {el:this, attr:attrs});
				//hide the original (if not already hidden) and remove its id...
				original.hide().removeAttr('id');
				//move the convertable LI contents...
				convertables.each(function(){
						$(this).data( DATA_NAMES[2], $('<span/>').insertBefore(this)[0] ).appendTo(rtn);
					});
				//set the id from the original, store the original, and insert the new...
				rtn.attr('id', id).insertBefore(original);
				//store the new with the original...
				original.data(DATA_NAMES[1], rtn[0]);
				return rtn[0];
			},
/** returns a timestamp; if Dock is supplied and there is anything left of a previous timestamp - when
 *  duration is added - then subtract that remainder from the new timestamp
 *  Explain : if you go on then off the menu (before the expansion has completed), it makes the time
 *            period for the collapse the same as the time used for the partial expansion
 * @private
 * @param {object} Dock Dock object
 * @return {integer} New timestamp
 */
			GET_TIME = function(Dock){
				var rtn = $.now(), //jquery v1.4.3+
						prevWillLapse = Dock ? Dock.Stamp + Dock.Opts.duration : 0;
				if(prevWillLapse > rtn){
					rtn -= prevWillLapse - rtn;
				}
				return rtn;
			},
/** the onload handler for images; stores width/height, and runs $.jqdock.init() (on a timeout) if 
 *  all images for a dock are loaded
 * @private
 * @this {element} The image element
 * @param {object} ev jQuery event object
 */
			IMAGE_ONLOAD = function(ev){
				//store 'large' width and height...
				var data = ev.data,
						Dock = DATA($(data.menu)),
						elData, op;
				if(Dock){
					elData = Dock.Elem[data.idx];
					op = Dock.Opts;
					if(op.sizeMax){
						//I can use KEEP_PROPORTION here, but in the same way that init() does for Opts.size, I
						//need to use the vh inverse
						$.extend(elData, KEEP_PROPORTION(this, op.sizeMax, VERTHORZ[VERTHORZ[op.vh].inv]), op.capSizing);
					}else{
						elData.width = this.width;
						elData.height = this.height;
					}
					if(!--Dock.Load){ //check to see if all images are loaded...
						WIN_TIMEOUT(function(){ $.jqdock.init(data.menu); }, 0);
					}
				}
			},
/** sets class and triggers event indicating whether an item is expanded or not
 * @private
 * @param {object} Dock Dock object
 * @param {object} el Item object
 * @param {boolean} off Switch indicating 'not expanded'
 */
			ITEM_EXPANSION = function(Dock, el, off){
				//note : since more than one item (.jqDockItem) can be 'active' at any time, the jqDockExpand
				//class merely indicates that an item is no longer 'at rest', ie. is in some state of expansion
				el.Item.toggleClass(CLASSES[5], !off); //jqDockExpand
				//trigger dockitem (on the original menu element), giving the image in question's DOM element and
				//whether the item is 'expanded' (true) or not (false)...
				Dock.Menu.trigger('dockitem', [el.Img[0], !off]);
			},
/** a label click handler that triggers its related image's click handler
 * @private
 * @this {element} The DOM element (label) the handler was bound to
 * @return {boolean} False
 */
			LABEL_CLICK = function(){
				//note : do NOT return false, because doing so prevents anchors being notified of clicking on labels!
				$(this).prev('img').triggerHandler('click');
//A bit more detail is needed here (for me mainly!)...
//Using trigger(), if a click handler is bound to the parent anchor, it will actually get notified of 2 clicks:
// - one is the bubbled click on the label, and
// - the other is the bubbled click on the image, that was script-trigger()ed by the click on the label.
//However, because triggering a click on an anchor will *not* make it perform its default action (which
//is to navigate to wherever it's supposed to navigate to), only the first 'natural' bubbled click will
//actually invoke the anchor's href as a target for the browser. Of course, if this handler returns false
//then that first event never reaches the anchor (which was the problem with v1.5, and 'fixed' in v1.7).
//The solution is therefore to change trigger() to triggerHandler(), which does not bubble events up the DOM
//thereby removing the second notification to the anchor's click handler (if set).
			},
/** shows/hides a label
 * @private
 * @param {object} Dock Dock object
 * @param {integer} [show] Show label
 */
			LABEL_SHOW = function(Dock, show){
				var item = Dock.Elem[Dock.Current];
				if(item && Dock.Opts.labels && !Dock.Opts.permanentLabels){
					item.Label.el.toggle(!!show);
				}
			},

/** translates (without affecting) XY[0] or XY[1] into an offset within div.jqDock
 *  note: doing it this way means that all attenuation is against the initial (shrunken) image positions,
 *  but it saves having to find every image's offset() each time the cursor moves or an image changes size!
 * @private
 * @param {object} Dock Dock object
 * @return {number} Translated mouse offset, or -1 if outside dock
 */
			RELATIVE_XY = function(Dock){
				var VH = VERTHORZ[Dock.Opts.vh], //convenience
						rtn = -1,
						//distance into the menu from the leading edge of first element in menu...
						offset = XY[VH.xy] - Dock.Elem[0].Item.offset()[VH.tl],
						i, el, padding, majorWidth;
				if(offset >= 0){
					for(i = 0 ; rtn < 0 && i < Dock.ElCt; i++){
						el = Dock.Elem[i];
						padding = el.Pad[VH.lead] + el.Pad[VH.trail];
						majorWidth = el.Major + padding;
						if(offset < majorWidth){
							//we've found the element that the mouse is currently on (which may or may not be the same as Current)
							if(i !== Dock.Current){ 
								//if its not the same as Current, make sure the label is hidden and reset Current...
								LABEL_SHOW(Dock); //hide
								Dock.Current = i;
							}
							rtn = el.Offset + (offset * (el.Initial + padding) / majorWidth);
						}else{
							offset -= majorWidth;
						}
					}
				}
				return rtn;
			},
/** removes ALL text nodes from the menu, so that we don't get spacing issues between menu elements
 * @private
 * @param {element} el DOM Element
 * @recursive
 */
			REMOVE_TEXT = function(el){
				var i = el.childNodes.length, j;
				while(i){
					j = el.childNodes[--i];
					if((j.childNodes||[]).length){
						REMOVE_TEXT(j);
					}else if(j.nodeType === 3){
						el.removeChild(j);
					}
				}
			},
/** initial display of the menu, copes with visibility:hidden as well as display:none
 * @private
 * @param {object} Menu jQuery object of menu element
 */
			REVEAL_MENU = function(Menu){
				Menu.css({visibility:'visible'}).show();
			},
/** if appropriate, sets an idle timer to trigger a dockidle
 * @private
 * @param {object} Dock Dock object
 */
			SET_IDLER = function(Dock){
				var idleDelay = Dock.Opts.idle;
				if(idleDelay){
					CLEAR_TIMER(Dock, 0); //Idler
					//set Idler timer...
					Dock[TIMERS[0]] = WIN_TIMEOUT(function(){
							Dock.Menu.trigger(CUSTOMEVENTS[1]); //dockidle
						}, idleDelay);
				}
			},
/** calculates the image sizes according to the current (translated) position of the cursor within div.jqDock
 *  result stored in Final for each menu element
 * @private
 * @param {object} Dock Dock object
 * @param {number} [relxy] Translated cursor offset in main axis
 */
			SET_SIZES = function(Dock, relxy){
				var op = Dock.Opts, //convenience
						wh = VERTHORZ[op.vh].wh, //convenience
						i = Dock.ElCt,
						el, ab, newFinal, oscillate;
				//if not forced, use current translated cursor position (main axis)...
				relxy = relxy || relxy === 0 ? relxy : RELATIVE_XY(Dock);
				while(i--){
					el = Dock.Elem[i];
					newFinal = el.Initial;
					if(relxy >= 0){
						ab = Math.abs(relxy - el.Centre);
						//if we're smack on or beyond the attenuation distance then set to the min dim
						//ensure Final ends up as an integer to avoid 'flutter'
						if(ab < op.distance){
							newFinal = el[wh] - Math.round((el[wh] - el.Initial) * Math.pow(ab, op.coefficient) / op.attenuation); 
						}
						//need to check for oscillation, where, for example, the Final dimension gets changed by a pixel,
						//which changes the relative position within the dock by a pixel, which changes the Final dimension
						//back by a pixel, which changes the relative position within the dock, which changes... etc, etc, etc!
						//it doesn't happen very often but it does happen!
						if(i === Dock.Current){
							if(!op.noAntiFlutter){
								oscillate = [XY[VERTHORZ[op.vh].xy], Dock.Current, newFinal].join(',');
								if(oscillate === Dock.ToFro[0] && newFinal !== Dock.ToFro[2]){
									newFinal = Dock.ToFro[2];
								}else{
									Dock.ToFro = [Dock.ToFro[1], oscillate, newFinal];
								}
							}
						}
					}
					el.Final = newFinal;
				}
			},
/** dummy function, simply returns labelText (for when options.setLabel is not provided)
 * @private
 * @this {element} original menu element
 * @param {string} labelText Current label text for menu option
 * @param {integer} indx Index of the menu option within the menu
 * @param {element} container DOM element div.jqDockLabelText
 * @return {string} labelText
 */
			TRANSFORM_LABEL = function(labelText, indx, container){
				return labelText;
			},
/** sets the css for an individual image wrapper to effect its change in size
 *  'dim' is the new value for the main axis dimension as specified in VERTHORZ[Opts.vh].wh, so
 *  the margin needs to be applied to the inverse dimension!
 *  note: 'force' is only set when called from initDock() to do the initial shrink
 * @private
 * @param {object} Dock Dock object
 * @param {integer} idx Image index
 * @param {integer} dim Main axis dimension of image
 * @param {boolean} force Force change even if no size difference
 */
			CHANGE_SIZE = function(Dock, idx, dim, force){
				var el = Dock.Elem[idx], //convenience
						op = Dock.Opts, //convenience
						yard = Dock.Yard, //convenience
						dockBorders = Dock.Border, //convenience
						VH = VERTHORZ[op.vh], //convenience
						invVH = VERTHORZ[VH.inv], //convenience
						labels = op.labels, //convenience
						currentEl = Dock.Elem[Dock.Current], //convenience
						srcDiff = el.src !== el.altsrc,
						bdr, css, diff, trail, i, j, label, labelElem;
				if(force || el.Major !== dim){
					//horizontal menus in IE quirks mode require border widths (if any) of the Dock to be added to the Dock's main axis dimension...
					bdr = ($.boxModel || op.vh === 'v') ? 0 : dockBorders[VH.lead] + dockBorders[VH.trail];
					//check for first (unforced) step of an expansion...
					if(!force && el.Major === el.Initial){
						//switch image source to large if it's different to small source...
						if(srcDiff){
							el.Img[0].src = el.altsrc;
						}
						ITEM_EXPANSION(Dock, el); //on
					}
					Dock.Spread += dim - el.Major; //adjust main axis dimension of dock
					css = KEEP_PROPORTION(el, dim, VH);
					diff = op.size - css[invVH.wh];
					//add minor axis margins according to alignment...
					//note: where diff is an odd number of pixels, for 'middle' or 'center' alignment put the odd pixel in the 'lead' margin
					i = {middle:1, center:1, top:2, left:2}[op.align] || 0;
					if(i > 1){ //set bottom or right margin
						css['margin' + TRBL[invVH.trail]] = diff;
					}else if(i){ //set top & bottom or left & right margins
						//v1.7 : handle 'bias' option allowing percentage offsets for middle & center (default=50)
						//note: it might seem a bit odd using (100 - ...) and calculating trail first, but this ensures that
						//any odd pixel from rounding is placed in the 'lead' margin
						trail = Math.round(diff * (100 - op.bias) / 100);
						css['margin' + TRBL[invVH.lead]] = diff - trail;
						css['margin' + TRBL[invVH.trail]] = trail;
					}else{ //set top or left margin (op.align = 'bottom' or 'right')
						css['margin' + TRBL[invVH.lead]] = diff;
					}
					//set dock's main axis dimension (if it's changed, or if force and this is first menu item)...
					if (dim !== el.Major || (force && !idx)) {
						if(op.flow){
							//if we ARE running flow, then the wrapper dimensions must be set so as to precisely contain the dock...
							yard.parent()[VH.wh](Dock.Spread + dockBorders[VH.lead] + dockBorders[VH.trail]);
						}
						yard[VH.wh](Dock.Spread + bdr);
					}
					//change image wrapper size and margins...
					el.Wrap.css(css);
					//set dock's main axis 'lead' offset (not negative!)...
					if(!op.flow){
						//if we are NOT running flow (which is the default) then the dock needs to be centered within its wrapper...
						yard.css(VH.tl, Math.floor(Math.max(0, (Dock[VH.wh] - Dock.Spread) / 2)));
					}

					//reposition the label if need be...
					if(Dock.OnDock){
						//only labels with middle and/or center alignment need re-positioning because css handles the corners
						if(currentEl && labels){
							label = currentEl.Label;
							labelElem = label.el;
							//check to see if the information required for a middle/centred label has already been gathered...
							//note : middle/centred labels can not be set up while the dock is display:none
							if(label.mc){
								label.mc = 0;
								//if labels are being aligned middle and/or centre then we need to find any user-styled padding
								//and width/height, and store the overall dimensions (incl. padding) for this image's label, so
								//that we don't need to do this next time...
								for(i in VERTHORZ){
									label[i] = labelElem[VERTHORZ[i].wh]();
									for(j in {lead:1, trail:1}){
										label[i] += AS_INTEGER(labelElem.css('padding' + TRBL[VERTHORZ[i][j]]));
									}
								}
							}
							//note : if vertically or horizontally centred then centre is based on the IMAGE only
							//note : .xy is 0 on horizontal menus, 1 on vertical menus (and vice versa for [.inv].xy!)...
							if(labels.charAt(0) === 'm'){
								labelElem.css({top: Math.floor((currentEl[AXES[invVH.xy]] - label.v) / 2)});
							}
							if(labels.charAt(1) === 'c'){
								labelElem.css({left: Math.floor((currentEl[AXES[VH.xy]] - label.h) / 2)});
							}
						}
						if(!Dock.Stamp){
							LABEL_SHOW(Dock, 1); //show
						}
					}

					//store new dimensions...
					el.Major = dim; //main axis
					el.Minor = css[invVH.wh]; //minor axis
					//check for last (unforced) step of a shrink...
					if(!force && dim === el.Initial){
						//switch image source to small if it's different to large source...
						if(srcDiff){
							el.Img[0].src = el.src;
						}
						ITEM_EXPANSION(Dock, el, 1); //off
					}
					css = null;
				}
			},
/** modifies the target sizes in proportion to 'duration' if still within the 'duration' period following a mouseenter/leave;
 *  calls CHANGE_SIZE() for each menu element (if more than Opts.step ms since mouseenter/leave)
 * @private
 * @param {object} Dock Dock object
 */
			FACTOR_SIZES = function(Dock){
				var op = Dock.Opts, //convenience
						VH = VERTHORZ[op.vh],
						lapse = op.duration + op.step,
						i, el, sz, stepsLeft;
				if(Dock.Stamp){
					lapse = GET_TIME() - Dock.Stamp;
					//there's no point continually checking Date once op.duration has passed...
					if(lapse >= op.duration){
						Dock.Stamp = 0;
					}
				}
				if(lapse >= op.step){ //only if Opts.step ms have passed since last mouseenter/leave
					stepsLeft = (op.duration - lapse) / op.step;
					for(i = 0 ; i < Dock.ElCt; i++){ //must go through the elements in logical order
						el = Dock.Elem[i];
						sz = el.Final - el.Major;
						sz = (sz && stepsLeft > 1) ? el.Major + Math[sz < 0 ? 'floor' : 'ceil'](sz / stepsLeft) : el.Final;
						CHANGE_SIZE(Dock, i, sz); //...will set .Major to sz
					}
					//tweak 'best guess':
					//having changed all item sizes within the dock, if Spread is greater than main axis dimension, adjust wrap dimension...
					if(Dock.Spread > Dock[VH.wh]){
						Dock.Yard.parent()[VH.wh](Dock.Spread + Dock.Border[VH.lead] + Dock.Border[VH.trail]);
						Dock[VH.wh] = Dock.Spread;
					}
				}
			},
/** handles movement of the mouse within a dock, and tidies up after entry (and halt) into a dock;
 *  clears its own timer, runs SET_SIZES, then if not complete, runs FACTOR_SIZES and then itself on a timer 
 * @private
 * @param {object} Dock Dock object
 * @param {number} [relxy] Translated cursor offset in main axis (when provided to OVER_DOCK)
 */
			IN_DOCK = function(Dock, relxy){
				var el = Dock.Elem,
						i = el.length;
				CLEAR_TIMER(Dock, 2); //Indock
				if(Dock.OnDock && !Dock.Stamp){
					SET_SIZES(Dock, relxy);
					while(i && el[i - 1].Major === el[i - 1].Final){ --i; }
					if(!i){
						LABEL_SHOW(Dock, 1); //show
					}else{
						FACTOR_SIZES(Dock);
						//set Indock timer...
						Dock[TIMERS[2]] = WIN_TIMEOUT(function(){ IN_DOCK(Dock, relxy); }, Dock.Opts.step);
					}
				}
			},
/** called when cursor goes outside menu, and checks for completed shrinking of all menu elements;
 *  calls FACTOR_SIZES() (with revers set) on any menu element that has not finished shrinking
 *  calls itself on a timer to complete the shrinkage
 * @private
 * @param {object} Dock Dock object
 * @param {boolean} noIdle Can idler be set
 */
			OFF_DOCK = function(Dock, noIdle){
				var el = Dock.Elem,
						i = el.length;
				if(!Dock.OnDock){
					while(i && el[i - 1].Major <= el[i - 1].Initial){ --i; }
					//this is here for no other reason than that early versions of Opera seem to leave 
					//a 'shadow' residue of the expanded image unless/until this function is called!...
					RELATIVE_XY(Dock);
					if(!i){ //complete
						//reset everything back to 'at rest' state...
						Dock.Stamp = 0;
						i = el.length;
						while(i--){
							el[i].Major = el[i].Final = el[i].Initial;
						}
						Dock.Current = -1;
						if(!noIdle){
							SET_IDLER(Dock);
						}
					}else{
						FACTOR_SIZES(Dock);
						//set Offdock timer...
						Dock[TIMERS[4]] = WIN_TIMEOUT(function(){ OFF_DOCK(Dock, noIdle); }, Dock.Opts.step);
					}
				}
			},
/** checks for completed expansion (if OnDock)
 *  runs SET_SIZES() then, if not completed, runs FACTOR_SIZES() and then itself on a timer
 * @private
 * @param {object} Dock Dock object
 * @param {number} [relxy] Translated cursor offset in main axis
 */
			OVER_DOCK = function(Dock, relxy){
				var el = Dock.Elem,
						i = el.length;
				if(Dock.OnDock){
					SET_SIZES(Dock, relxy);
					while(i && el[i - 1].Major === el[i - 1].Final){ --i; }
					if(!i || !Dock.Stamp){ //complete, or beyond 'duration'
						Dock.Stamp = 0;
						IN_DOCK(Dock, relxy);
					}else{
						FACTOR_SIZES(Dock);
						//set Overdock timer...
						Dock[TIMERS[3]] = WIN_TIMEOUT(function(){ OVER_DOCK(Dock, relxy); }, Dock.Opts.step);
					}
				}
			},
/** actions for any type of mouse event
 * @private
 * @param {integer} etype Type of event as index into MOUSEEVENTS array
 * @param {object} Dock Dock object
 * @param {integer} idx Menu item id or -1
 * @param {integer} fake Set - usually to 1 - if called as a result of inactivity or when faking a mouseenter
 */
			DO_MOUSE = function(etype, Dock, idx, fake){
				var el = Dock.Elem, //convenience
						i = el.length;
				//mouseenter...
				if(etype === 0){
					Dock.OnDock = 1;
					if(Dock.Current >= 0 && Dock.Current !== idx){
						LABEL_SHOW(Dock); //hide
					}
					Dock.Current = idx;
					//if fake is set greater than 1 then timestamp is set to zero (no animation)...
					Dock.Stamp = fake && fake > 1 ? 0 : GET_TIME(Dock);
					OVER_DOCK(Dock, fake ? el[idx].Centre : null);
				}
				//mousemove...
				if(etype === 1){
					if(idx !== Dock.Current){ //mousemove from one item onto another
						LABEL_SHOW(Dock); //hide
						Dock.Current = idx;
					}
					IN_DOCK(Dock);
				}
				//mouseleave...
				if(etype === 2){
					CLEAR_TIMER(Dock, 1); //Inactive
					Dock.OnDock = 0;
					LABEL_SHOW(Dock); //hide
					Dock.Stamp = GET_TIME(Dock);
					while(i--){
						el[i].Final = el[i].Initial;
					}
					OFF_DOCK(Dock, !!fake); //clears Current when complete
				}
			},
/** handler for all bound mouse events (move/enter/leave)
 * @private
 * @this {element} div.jqDockItem
 * @param {object} ev jQuery Event object
 * @return {boolean} false
 */
			MOUSE_HANDLER = function(ev){
				//Note : don't return false, otherwise handlers listening on docksleep and then, for example,
				//       checking a mouseover on div.jqDock in order to 'bring back' a hidden menu, would
				//       not receive notification of the mouseover because it would be blocked here
				var Dock = DATA(MENU_OR_ITEM(this)),
						idx = ITEM_INDEX(this),
						doMse = -1,
						onDock;
				if(Dock){
					if(Dock.Asleep){ //buffer it?...
						if(!Dock.Opts.noBuffer){ //...yes...
							Dock.Doze = {
								El:this,
								type:ev.type,
								pageX:ev.pageX,
								pageY:ev.pageY
								};
						}
					}else{
						onDock = Dock.OnDock;
						CLEAR_TIMER(Dock, 0); //Idler
						XY = [ev.pageX, ev.pageY];
						if(ev.type === MOUSEEVENTS[2]){//=mouseleave
							if(onDock){
								doMse = 2; //mouseleave
							}else{
								SET_IDLER(Dock);
							}
						}else{ //=mousemove or mouseenter...
							if(Dock.Opts.inactivity){
								CLEAR_TIMER(Dock, 1); //Inactive
								//set Inactive timer...
								Dock[TIMERS[1]] = WIN_TIMEOUT(function(){ 
										DO_MOUSE(2, Dock, idx, 1); //mouseleave (faked)
									}, Dock.Opts.inactivity);
							}
							if(ev.type === MOUSEEVENTS[1]){ //=mousemove
								if(idx < 0){
									if(onDock && Dock.Current >= 0){ //off of current
										doMse = 2; //mouseleave
									}
								}else if(!onDock || Dock.Current < 0){ //instant re-entry or no current
									doMse = 0; //mouseenter
								}else{ //change of current or moving within current
									doMse = 1; //mousemove
								}
							}else if(idx >= 0 && !onDock){ //mouseenter...
								doMse = 0; //mouseenter
							}
						}
						Dock.Doze = null;
						if(doMse >= 0){
							DO_MOUSE(doMse, Dock, idx);
						}
					}
				}
			},
/** handler for the docknudge, dockidle and dockfreeze events
 * @private
 * @this {element} The original menu DOM element
 * @param {object} ev jQuery event object
 */
			LISTENER = function(ev){
				var Menu = $(this),
						Dock = DATA(Menu),
						frosty = ev.type === CUSTOMEVENTS[2], //dockfreeze?
						param = frosty ? 'freeze' : 'sleep',
						stateChange, doze;
				if(Dock){
					//attempts to 'nudge' the dock awake...
					if(ev.type === CUSTOMEVENTS[0]){ //docknudge
						param = Dock.Frozen ? 'thaw' : 'wake';
						//if Asleep, check for onWake returning a false - to stay asleep - and
						//trigger a dockwake event if not still asleep...
						if(Dock.Asleep){
							Dock.Asleep = Dock.Opts.onWake.call(this, param) === false;
							if(!Dock.Asleep){
								//always clear frozen...
								Dock.Frozen = !Menu.trigger('dockwake', [param]);
							}
						}
						if(!Dock.Asleep){
							//start (or reset) idling now...
							SET_IDLER(Dock);
							//if we have buffered mouse event, run it...
							doze = Dock.Doze;
							if(doze){
								MOUSE_HANDLER.call(doze.El, doze);
							}
						}
					//...must be dockidle or dockfreeze event type...
					}else{ //attempts to send the dock to sleep...
						CLEAR_TIMER(Dock, 0); //Idler : needed if triggered by the calling program
						//NB: returning false from onSleep() prevents the dock going to sleep/freezing, but
						//it does NOT reset the idle timer!

						//onSleep will only get called - and docksleep only get triggered - if we have a change of state, ie...
						// - if not already asleep, or
						// - if dockfreeze and not already frozen
						//this means that if you idle a non-sleeping dock, then freeze it, onSleep will get called twice (once for each);
						//but if you freeze, then idle, then freeze, onSleep will only get called for the first freeze.
						stateChange = !Dock.Asleep || (frosty && !Dock.Frozen);
						if(!stateChange || Dock.Opts.onSleep.call(this, param) !== false){
							Dock.Asleep = !CLEAR_TIMER(Dock, frosty ? -1 : 1); //Inactive, or all if freezing
							Dock.Frozen = Dock.Frozen || frosty;
							if(stateChange){
								Menu.trigger('docksleep', [param]);
							}
							if(frosty){
								//need to clear the timestamp in case the dock was frozen during an automatic expansion/collapse...
								Dock.Stamp = Dock.OnDock = 0;
							}else{
								DO_MOUSE(2, Dock, 0, 1); //fake a mouseleave as if it were due to inactivity
							}
						}
					}
				}
			}; //end of var declarations

/**
 * The main $.jqdock object
 * @private
 * @return {object}
 */
	$.jqdock = $.jqDock = (function(){
		return {
			version : '2.0.2',
			defaults : {          //can be set at runtime, per menu
				size : 48,          //[px] maximum minor axis dimension (when at rest) of image (width or height depending on 'align' : vertical menu = width, horizontal = height)
				sizeMax : 0,        //[px] maximum minor axis dimension of a fully-expanded image (width or height depending on 'align' : vertical menu = width, horizontal = height)
				capSizing : 0,      //changes the calculation of maximum minor axis dimensions (size and sizeMax options) from absolute (ie. regardless of whether the value is less than or greater than the option value) to a cap (ie. only if greater than); default is absolute
				distance : 72,      //[px] attenuation distance from cursor
				coefficient : 1.5,  //attenuation coefficient
				duration : 300,     //[ms] duration of initial expansion and off-menu shrinkage
				align : 'bottom',   //[top/middle/bottom or left/center/right] fixes horizontal/vertical expansion axis
				labels : 0,         //enable/disable display of a label on the current image; (true) to use default position, or string to specify
				source : 0,         //function: given scope of relevant image element; passed index of image within menu; required to return image source path, or false to use original
				loader : 0,         //overrides useJqLoader if set to 'image' or 'jquery'
				inactivity : 0,     //[ms] duration of inactivity (no mouse movement) after which any expanded images will collapse; 0 (zero) disables the inactivity timeout
				fadeIn : 0,         //[ms] duration of the fade-in 'reveal' of the jqDocked menu; set to zero for instant 'show'
				fadeLayer : '',     //if fadeIn is set, this can change the element that is faded; the default is the entire original menu; alternatives are 'wrap' (.jqDockWrap element) or 'dock' (.jqDock element)
				step : 50,          //[ms] the timer interval between each step of shrinkage/expansion
				setLabel : 0,       //function for transforming label text (ie. title) when initially building the label;
														//  this is provided so that if the label requires HTML, the transform function can set 
														//  it rather than having to put it in the title field and thereby make the markup invalid.
														//  the called function will be given the scope (this) of the original menu element, and will be
														//  passed 4 arguments: 
														//   - the derived default text of the label (from the title of either the image or its parent anchor)
														//   - the (zero-based) index of the option within the menu
														//   - the outer DOM element of the target label, div.jqDockLabel
														//   - an array of HTML for creating the inner label container, div.jqDockLabelText (['<div class="jqDockLabelText">', '</div>'])
														//  the function should return either
														//   - the HTML string for the label, in which case jqDock will create the inner container and append the returned text to it
														//   - or false, in which case jqDock will do nothing further with the label setup
				flow : 0,           //alters the default dock behaviour such that the dock is NOT auto-centered and the wrap 
														//  element (.jqDockWrap, which a relatively positioned) expands and collapses to precisely
														//  contain the dock (.jqDock); this allows elements positioned around the docked menu to
														//  adjust their own relative position according to the current state of the docked menu
				idle : 0,           //[ms] duration of idle time after the mouse has left the menu (without re-entering, obviously!) before the docksleep event is triggered (on the original menu element)
				onReady : 0,        //function: called with scope of original menu element when dock has been initialised but not yet revealed (ie. before being shown)
														//  NB: the onReady() function is passed a single argument, 'ready', and can return false to cancel the 'reveal' of the menu and put the dock to sleep
				onSleep : 0,        //function: called with scope of original menu element when dock has been idle for the defined idle period and has therefore gone to sleep,
														//  or when either a sleep or freeze has been requested by the calling script (by triggering dockidle/dockfreeze, or commanding idle/freeze)
														//  NB: the onSleep() function is passed a single argument, 'sleep' or 'freeze', and can return false to cancel the sleep/freeze
				onWake : 0,         //function: called with scope of original menu element when dock is 'nudged' awake, but only triggered if the dock was asleep (incl. frozen) prior to the' nudge'
														//  NB: the onWake() function is passed a single argument, 'wake' or 'thaw', and can return false to cancel the wake-up (dock stays asleep/frozen)
				noBuffer : 0,       //disables the buffering of the last mouse event while the dock is asleep
				active : -1,        //index (zero-based) of the image required to be expanded on initial display
				bias : 50,          //percentage expansion (range 0-100) on the *leading* edge side, for middle and center aligned 
														//  expansion, providing a biased expansion instead of 50/50 from the mid-point
														//  NB: align:middle,bias:0 === align:top, and align:middle,bias:100 === align:bottom
														//  and vertically, align:center,bias:0 === align:left, and align:center,bias:100 === align:right
				noAntiFlutter : 0,  //allows disabling/bypassing of the oscillation checking in SET_SIZES()
				permanentLabels : 0 //keeps enabled labels on permanent display
			},
			nextId : 0,           //the id for the next Dock to be constructed
			useJqLoader : !$.browser || $.browser.opera || $.browser.safari, //use jQuery method for loading images, rather than "new Image()" method

/** once all images have been loaded (IMAGE_ONLOAD), it completes the setup of the dock menu
 *  note: unless all images get loaded, this won't get called and the menu will stay hidden!
 * @this {$.jqdock}
 * @param {element} menu DOM element of original menu
 */
			init : function(menu){
			//========================================
				var Menu = $(menu),
						Dock = DATA(Menu),
						op = Dock.Opts, //convenience
						VH = VERTHORZ[op.vh], //convenience
						invVH = VERTHORZ[VH.inv], //convenience
						borders = Dock.Border, //convenience
						fadeLayer = op.fadeLayer, //convenience
						labels = op.labels, //convenience
						numItems = Dock.ElCt,
						vanillaDiv = $('<div/>').css({position:'relative', padding:0}),
						vanillaClone = vanillaDiv.clone().css({margin:0, border:'0 none', backgroundColor:'transparent'}),
						offset = 0,
						i = 0,
						label, posBottom, posRight, labelText,
						j, k, el, wh, acc, upad, wrap, yard;
				// things will screw up if we don't clear text nodes...
				REMOVE_TEXT(menu);
				//double wrap, and set some basic styles on the dock elements, otherwise it won't work
				Menu.children()
					.each(function(i, kid){
							var wrap = Dock.Elem[i].Wrap = $(kid).wrap(vanillaClone.clone().append(vanillaClone.clone())).parent(); 
							if(op.vh === 'h'){
								wrap.parent().css('float', 'left');
							}
							$('*', wrap).css({
								position: 'relative',
								padding: 0,
								margin: 0,
								borderWidth: 0,
								borderStyle: 'none',
								verticalAlign: 'top',
								display: 'block',
								width: '100%',
								height: '100%'
								});
					});

				//resize each image and store various settings wrt main axis...
				while(i < numItems){
					el = Dock.Elem[i++]; //convenience
					upad = el.Pad; //convenience
					//resize the image wrapper to make the minor axis dimension meet the specified 'Opts.size'...
					wh = KEEP_PROPORTION(el, op.size, invVH, op.capSizing); //inverted!
					el.Major = el.Final = el.Initial = wh[VH.wh];
					//resize the image wrapper to its new shrunken setting, and store the outer wrap...
					el.Item = el.Wrap.css(wh).parent();
					//remove titles, alt text...
					el.Img.attr({alt:''}).parent('a').andSelf().removeAttr('title');
					//use inverts because we're after the minor axis dimension...
					Dock[invVH.wh] = Math.max(Dock[invVH.wh], op.size + upad[invVH.lead] + upad[invVH.trail]);

					el.Offset = offset;
					el.Centre = offset + upad[VH.lead] + (el.Initial / 2);
					offset += el.Initial + upad[VH.lead] + upad[VH.trail];
				}

				//'best guess' at calculating max 'spread' (main axis dimension - horizontal or vertical) of menu:
				//for each img element of the menu, call SET_SIZES() with a forced cursor position of the centre of the image;
				//SET_SIZES() will set each element's Final value, so tally them all, including user-applied padding, to give
				//an overall width/height for this cursor position; set dock width/height to be the largest width/height found;
				//repeat, with a forced cursor position of the leading edge of image
				i = 0;
				while(i < numItems){
					el = Dock.Elem[i++];
					upad = el.Pad[VH.lead] + el.Pad[VH.trail]; //user padding in main axis
					//tally the minimum widths...
					Dock.Spread += el.Initial + upad;

					//for override cursor positions of Centre and Offset...
					for(k in {Centre:1, Offset:1}){
						//set sizes with an overridden cursor position...
						SET_SIZES(Dock, el[k]);
						//tally image widths/heights (plus padding)...
						acc = 0; //accumulator for main axis image dimensions
						j = numItems;
						while(j--){
							//note that Final is an image dimension (in main axis) and does not include any user padding...
							acc += Dock.Elem[j].Final + upad;
						}
						//keep largest main axis dock dimension...
						if(acc > Dock[VH.wh]){
							Dock[VH.wh] = acc;
						}
					}
				} //... i is now numItems

				//reset Final for each image...
				while(i){
					el = Dock.Elem[--i];
					el.Final = el.Initial;
				} //... i is now 0

				Menu.wrapInner(
					vanillaDiv.addClass(CLASSES[6]).append( //nb: outer is .jqDockWrap
//						vanillaClone.attr({id:'jqDock', 'class':'jqDock', style:''}).css({
						vanillaClone.attr({id:Dock.Id, 'class':CLASSES[8], style:''}).css({ //nb: inner is #jqDockN.jqDock
								position: 'absolute',
								top: 0,
								left: 0,
								padding: 0,
								margin: 0,
								overflow: 'visible',
								height: Dock.height,
								width: Dock.width
								})
							)
					);
				yard = Dock.Yard = $('#' + Dock.Id);
				//now that we have div.jqDock, let's see if the user has applied any css border styling to it...
				j = 4;
				while(j--){
					borders[j] = AS_INTEGER(yard.css('border' + TRBL[j] + 'Width'));
				}
				wrap = yard.parent()
					.width(Dock.width + borders[1] + borders[3]) //Right and Left
					.height(Dock.height + borders[0] + borders[2]); //Top and Bottom

				//shrink all images down to 'at rest' size, and add appropriate identifying class...
				while(i < numItems){
					el = Dock.Elem[i];
					//apply the image's user-applied padding to the outer element wrapper (div.jqDockItem)...
					upad = el.Item.css('padding', el.Pad.join('px ') + 'px');

					CHANGE_SIZE(Dock, i, el.Final, true); //force
					//give a mouse class to both the image and the outer element wrapper (to handle any user padding)...
					//v2.0 : ...and give the outer element wrapper a class of jqDockItem...
					upad.addClass(CLASSES[1]).add(el.Img).addClass(CLASSES[2] + i);

					//create and append the label
					//===========================
					label = el.Label;
					//labels always get created (as of v1.6), and get shown if they are enabled...
					//   the label is partially created, *then* option.setLabel is called and the result, if there is one,
					//   is used to help create the rest of the label, eg.
					//     - create outer label container, div.jqDockLabel
					//     - set the outer container's css and add a click handler
					//     - call option.setLabel
					//     - if setLabel returns false
					//       - do nothing else to the label
					//       - create the inner label container (div.jqDockLabelText) with the returned text inside it
					//   this means that option.setLabel can use DOM manipulation if wants to, and return false to prevent
					//   anything further being done to the label by jqDock, and labels being 'shown' only depends on labels
					//   being enabled!
					//   NOTE : if setLabel() returns false, then it (setLabel) is responsible for creating the inner 
					//          container (if it still needs it)

					//create the label's *outer* container (div.jqDockLabel)...
					label.el = $('<div/>').css({position:'absolute', margin:0}).addClass(CLASSES[3] + ' ' + CLASSES[3] + el.Link)
						//insert the label after the image element, *then* hide it...
						.insertAfter(el.Img).hide()
						//assign the click handler here (which, as of v1.7, does NOT return false), regardless of
						//whether labels are enabled or not...
						.on('click', LABEL_CLICK);
						//Note that the click handler is on div.jqDockLabel, not div.jqDockLabelText! This should mean that
						//the calling script can put its own click handler(s) on div.jqDockLabelText or its contents without
						//having to remove jqDock's LABEL_CLICK handler?
					if(labels){
						//note: any label setting involving middle/center gets handled in CHANGE_SIZE() the first time
						//      the label is required
						posBottom = labels.charAt(0) === 'b';
						posRight = labels.charAt(1) === 'r';
						//position the label...
						label.el.css({
							top:    posBottom ? 'auto' : 0,
							left:   posRight  ? 'auto' : 0,
							bottom: posBottom ? 0 : 'auto',
							right:  posRight  ? 0 : 'auto'
							});
					}
					//get the content for the *inner* label container...
					//NB: 4th parameter (DOM element div.jqDockLabel) added as of v1.6
					labelText = op.setLabel.call(menu, el.Title, i, label.el[0]);
					if(labelText !== false){
						//if there is label content (as an HTML string!) then insert it with the inner container...
						$('<div/>').addClass(CLASSES[3] + 'Text').html(labelText.toString()).appendTo(label.el);
					}
					//if permanentLabels is set, show the label...
					if(labels && op.permanentLabels){
						label.el.show();
					}
					//end of label creation
					//=====================
					++i;
				}

				//bind dock listener events to the original menu element...
				el = Menu.on(CUSTOMEVENTS.join(' '), LISTENER);
				//delegate mouse events on items (.jqDockItem) to the dock, and set filter:inherit on
				//everything below the dock (see below)...
				yard.on(MOUSEEVENTS.join(' '), DOTCLASS[1], MOUSE_HANDLER).find('*').css({filter:'inherit'});

				//if we have a request for an 'active' image...
				if(Dock.Elem[op.active]){
					//fake a mouseeenter, with no timestamp so no animation...
					DO_MOUSE(0, Dock, op.active, 2);
				}

				//Notes (to self) on Opacity
				//==========================
				//In IE8, non-statically positioned child elements do not inherit opacity; a way round this
				//is to set filter:inherit on child elements
				//Further complications with IE's opacity handling :
				//When animating opacity (as opposed to doing a fadeIn) the alpha filter of the animated element *must*
				//be cleared (='' or ='inherit') on completion back to opacity 1. Otherwise, in IE7 the element will not allow
				//children (in this case, the images) to be visible beyond its bounds (ie. expanding a menu item gets the image
				//chopped off at the edge of jqDock); in IE8, the image does expand ok, but leaves 'shadows' when collapsing!
				//Another complication is that jQuery does not recognise that filter can contain anything other than an
				//'alpha(opacity=xxx)' value, so when the filter is set to 'inherit', jQuery animates opacity by *appending*
				//the 'alpha(...)' value to the current 'inherit' value (eg. filter:'inheritalpha(...)' 
				//So ...
				//... on the assumption that nothing outside of jDock is going to want to individually fade 
				//anything below the .jqDock, I'm setting filter:inherit on all its children, for IE8's sake.
				//This is just in case anyone uses docksleep to perform a fade on .jqDock; if they do a fade
				//on either .jqDockWrap or the original menu element, then they may have to set (and probably
				//clear) filter:inherit on .jqDock, or .jqDock and .jqDockWrap (respectively) themselves!

				//show the menu now?...
				//if onReady returns false then the dock goes to sleep and will require a 'nudge' at some point to wake it up
				Dock.Asleep = op.onReady.call(menu, 'ready') === false;
				if(!Dock.Asleep){
					if(fadeLayer){
						//can only be 1 of menu/wrap/dock, and el is already set to menu...
						if(fadeLayer !== 'menu'){ //either dock or wrap...
							el = fadeLayer === 'dock' ? yard : wrap;
						}
						//.jqDockFilter is used so that I can ensure that only elements *below* .jqDock
						//have filter:inherit set; this is so that if the calling program uses docksleep
						//to fade out .jqDock I can at least ensure that it will work for IE8 (regardless
						//of the other problems with animating IE's opacity!)
						//Unfortunately, because of IE (grrr), we have to put the dock to sleep while the
						//fade is taking place. This is because if the user were to mouse-over the menu 
						//while it was still fading in, the menu element expansion would either be cut off
						//at the jqDockWrap boundary (IE6/7) or would leave a 'shadow' trail effect beyond
						//the jqDockWrap boundary as it shrank (IE8) ... due to the filters not being reset
						//until the end of the animation.
						//for .jqDock & .jqDockWrap...
//						Dock.Asleep = !!$('.jqDock,.' + CLASSES[6], el).addClass(CLASSES[4]).css({filter:'inherit'});
						Dock.Asleep = !!$(DOTCLASS[8] + ',' + DOTCLASS[6], el).addClass(CLASSES[4]).css({filter:'inherit'});
						el.css({opacity:0});
						REVEAL_MENU(Menu);
						el.animate(
							{opacity:1},
							op.fadeIn,
							function(){
								//clears filters and notifies readiness; scope (this) is the faded-in element
								var Menu = MENU_OR_ITEM(this);
								//remove any filters, and clear Asleep so that a docknudge won't do it's wake-up routine...
								DATA(Menu).Asleep = !$(DOTCLASS[4], this).add(this).css({filter:''}).removeClass(CLASSES[4]);
								//trigger dockshow (for the calling script) and docknudge (for me)...
								Menu.trigger('dockshow', ['ready']).trigger(CUSTOMEVENTS[0]);
							});
					}else{
						REVEAL_MENU(Menu);
						Menu.trigger('dockshow', ['ready']);
						SET_IDLER(Dock);
					}
				}
			}, //end function init()

/** returns (first) element, converted if it's a list (see CONVERT_LISTS())
 * @this {$.jqdock}
 * @param {element|array|selector} listEl DOM element, or array thereof, or selector, of original menu (a UL or OL list)
 * @return {element} The new DIV, or the conversion (if already converted), or the (first of) original element(s) (if not already a list)
 */
			convert : function(listEl){
				return CONVERT_LISTS.call($(listEl).get(0));
			}

			}; //end of return object
		}()); //run the function to set up $.jqdock

	/***************************************************************************************************
	*  jQuery.fn.jqDock()  and, as of v1.9, jQuery.fn.jqdock()
	*  ==================
	* STANDARD
	* usage:      $(selector).jqDock(options);
	* options:    see $.jqdock.defaults
  * returns:    $(selector)
  *             BUT as of v2.0.0 please note that the returned collection may not be the same as the
  *             input collection if the input contains convertable lists!
  *
  * ALTERNATE   ...provides a means for modifying image paths post-initialisation
  * usage:      $(image-selector).jqDock(options);
	* options:    object, with the following possible properties...
	*               src: {string|function} Path to 'at rest' image, or function returning a path
	*               altsrc: {string|function} Path to expanded image, or function returning a path
  * returns:    $(image-selector)
  * Note : image-selector *must* result in solely IMG element(s)
  * 
  * ALTERNATE2  ...provides a means for nudging a dock awake, or sending it to sleep
	*                (see Advanced documentation)
  * usage:      $(selector).jqDock('nudge'); //'nudges' dock awake
  *             $(selector).jqDock('idle'); //sends dock to sleep
  * returns:    $(selector)
  * Note : selector should be (or contain) already initialised dock(s), ie. classed with 'jqDocked'
	*
  * ALTERNATE3  ...a 'getter', providing a means for retrieving either a Dock's internal object, or
  *                an image's object from the Elem array
  *                (undocumented, but used in example.js)
  * usage:      $(menu-selector).jqDock('get');
  *             $(image-selector).jqDock('get');
  * returns:    {object} The object corresponding to the first (active) Dock in the $(menu-selector)
  *                      colection; or the object which is the element of the Elem array corresponding
  *                      to the first 'img' DOM element in the $(image-selector) collection
	* 
	* ALTERNATE4  ...provides a means for removing jqDock from a 'docked' element
	*                (see Advanced documentation)
	* usage:      $(selector).jqDock('destroy');
  * returns:    $(selector)
  * Note : selector should be (or contain) already initialised dock(s), ie. classed with 'jqDocked'
	* 
	* ALTERNATE5  ...provides a means for expanding (making active) an image
	* usage:      $(image-selector).jqDock('expand'); //with animation
	*             $(image-selector).jqDock('active'); //without animation
	* returns:    $(image-selector)
	* 
	* note: the aim is to do as little processing as possible after setup, because everything is
	* driven from the mousemove/enter/leave events and I don't want to kill the browser if I can help it!
	* hence the code below, and in $.jqdock.init(), sets up and stores everything it possibly can
	* which will reduce processing at runtime, and hopefully give as smooth animation as possible.
	***************************************************************************************************/
	$.fn.jqdock = $.fn.jqDock = function(opts){
		var rtn = this,
				isImage, Dock;
		/***************************************************************************************************
		* ALTERNATE2:
		* Accepts 'nudge', 'idle' or 'freeze'. Chainable.
		* 
		* Example:
		*   $('#menu').jqDock('nudge'); //wake from sleep
		*   $('#menu').jqDock('idle'); //send to sleep
		*   $('#menu').jqDock('freeze'); //freeze the dock
		***************************************************************************************************/
		if(opts === 'nudge' || opts === 'idle' || opts === 'freeze'){ //alternate usage 3 (nudge/idle/freeze)
			rtn.filter(DOTCLASS[0]).each(function(){ //only runs on an original menu element that has been docked (.jqDocked)
					LISTENER.call(this, {type:'dock'+opts});
				});
		/***************************************************************************************************
		* ALTERNATE4:
		* Accepts 'destroy'. Chainable
		* 
		* WARNING : as of v2.0, if the collection passed in contains a structure created from a list, then
		*           the returned chainable will contain the original list element in its place! This maintains
		*           consistency with what happens when a Dock is initialised, and also handles the fact that
		*           the converted structure is removed as part of the destroy processing.
		*           It also means that...
		*             $('ul').jqdock().jqdock('destroy').jqdock();
		*           ...will work!
		* 
		* Example:
		*   $('#menu').jqDock('destroy'); //remove jqDock functionality from the menu
		***************************************************************************************************/
		}else if(opts === 'destroy'){ //alternate usage 3 (destroy)
			rtn = rtn.map(function(){
					var Self = $(this).filter(DOTCLASS[0]),
							Dock = DATA(Self.removeClass(CLASSES[0])),
							convertedList = $.extend({}, Self.data(DATA_NAMES[3])),
							i, j, el, item;
					if(Self.length && Dock){
						//do this first to effectively disable all event handlers...
						Self.removeData(DATA_NAMES[0]);
						//clear any timers...
						CLEAR_TIMER(Dock, -1);
						//only remove the custom events that jqDock was listening for; if the calling
						//script bound listeners for the other custom events (show/sleep/wake) then it
						//is the calling script's responsibility to remove them (or not) as it wishes
						Self.off(CUSTOMEVENTS.join(' '), LISTENER);
						for(i = 0; i < Dock.ElCt; i++){
							el = Dock.Elem[i];
							//remove the label if it exists yet (click handler gets removed)...
							(el.Label.el || $()).remove();
							//put the original attributes back onto the image, and remove the jqDockMouseN class...
							item = el.Img;
							item.attr(el.Orig.i).removeClass(CLASSES[2] + i);
							if(!el.Orig.i.style){ //if there was no inline style, might as well remove the style attribute
								item.removeAttr('style');
							}
							//put the original attributes back onto the parent anchor (if present)...
							if(el.Link === 'Link'){
								item = item.parent();
								item.attr(el.Orig.a);
								if(!el.Orig.a.style){ //if there was no inline style, might as well remove it
									item.removeAttr('style');
								}
							}
							//move the anchor/image back up to the original menu element...
							//OR to the LI that it first came from (if the dock was a list conversion)...
							if(convertedList.el){
								$( item.data(DATA_NAMES[2]) ).after(item).remove();
							}else{
								Self.append(item);
							}
							//clear down...
							item = el.Label.el = el.Orig.i = el.Orig.a = null;
							for(j in el){
								el[j] = null;
							}
							el = null;
						}
						//remove the dock wrapper (.jqDockWrap), which clears mouse events...
						$(DOTCLASS[6], Self).empty().remove();
						j = Dock.Style;
						//if the dock was a conversion of a list, dump the dock and reinstate the list...
						if(convertedList.el){
							Self.empty().remove();
							el = $(convertedList.el).removeData(DATA_NAMES[1]).attr(convertedList.attr);
							if(!convertedList.attr.style){
								el.removeAttr('style');
							}
						//...otherwise, reinstate the original inline styling...
						}else if(j){
							Self.attr('style', j);
						}else{
							Self.removeAttr('style');
						}
						//clear down...
						for(i in Dock){
							Dock[i] = null;
						}
						Dock = el = null;
					}
					return convertedList.el || this;
				});
		/***************************************************************************************************
		* ALTERNATE5:
		* Accepts 'active' or 'expand'. Chainable
		* 
		* Example:
		*   $('#menu img').eq(1).jqDock('active'); //set the 2nd image instantly to fully expanded
		*   $('#menu img').last().jqDock('expand'); //animate the last image to fully expanded
		***************************************************************************************************/
		}else if(opts === 'active' || opts === 'expand'){
			rtn.each(function(){
					var Item = MENU_OR_ITEM(this, 1), //closest div.jqDockItem
							Menu = MENU_OR_ITEM(Item), //closest div.jqDocked
							Dock = DATA(Menu);
					if(Dock){
						//clear all timers...
						CLEAR_TIMER(Dock, -1);
						//if it wasn't already frozen, freeze it now and notify...
						if(!Dock.Frozen){
							Dock.Frozen = Dock.Asleep = !!Menu.trigger('docksleep', ['freeze']);
						}
						//fake a mouseenter, with animation dependent on value of opts ('active' = no animation)...
						DO_MOUSE(0, Dock, ITEM_INDEX(Item), opts === 'active' ? 2 : 1);
					}
				});
		/***************************************************************************************************
		* ALTERNATE3:
		* Accepts 'get'. Not chainable, returns object/null
		* 
		* Example:
		*   //to retrieve an item's original text used for the label (unmodified by setLabel option)...
		*   var labelText = $('#menu img:eq(2)').jqDock('get').Title;
		* Example:
		*   //to retrieve a dock's options...
		*   var options = $('#menu').jqDock('get').Opts;
		***************************************************************************************************/
		}else if(opts === 'get'){
			rtn = rtn.eq(0);
			isImage = rtn.is('img');
			Dock = DATA(isImage ? MENU_OR_ITEM(rtn) : rtn);
			//note : since all the information is now readily available on the menu's data(), there is no point
			//       extend()ing it to make it unmodifiable
			rtn = (Dock && isImage ? Dock.Elem[ ITEM_INDEX(MENU_OR_ITEM(rtn, 1)) ] : Dock) || null;
		/***************************************************************************************************
		* ALTERNATE:
		* If a function is provided, it will be called with scope of the image DOM element, and 2 parameters:
		* - current setting
		* - settingType, eg. 'src' or 'altsrc'
		*
		* Example (with strings):
		*   $('#menu img').eq(0).jqDock({src:'newpath.jpg', altsrc:'newexpanderpath.jpg'});
		* Example (with functions):
		*   fnChangePath = function(current, type){
		*       //always change altsrc, but only change src if image has a class of 'changeExpanded'...
		*       return type === 'altsrc' || $(this).hasClass('changeExpanded')
		*         ? current.replace(/old\.png$/, 'new.png')
		*         : current;
		*     };
		*   $('#menu img').jqDock({src:fnChangePath, altsrc:fnChangePath});
		***************************************************************************************************/
		}else if(rtn.length && !rtn.not('img').length){ //images only!!
			rtn.each(function(n, el){
					var Dock = DATA(MENU_OR_ITEM(el)),
							item = Dock ? Dock.Elem[ ITEM_INDEX(MENU_OR_ITEM(el, 1)) ] : 0, //use index of closest div.jqDockItem
							src = 0,
							atRest, str, v;
					opts = opts || {};
					if(item){
						atRest = item.Major === item.Initial;
						for(v in {src:1, altsrc:1}){
							if(opts[v]){
								str = ($.isFunction(opts[v]) ? opts[v].call(el, item[v], v) : opts[v]).toString();
								if(item[v] !== str){
									item[v] = str;
									src = (v === 'src' ? atRest : !atRest) ? v : src;
								}
							}
						}
						if(src){
							$(el).attr('src', item[src]);
						}
					}
				});
		/***************************************************************************************************
		* STANDARD:
		* Chainable.
		* 
		* WARNING : as of v2.0, if a list is passed in then the returned chainable will contain the
		*           converted element NOT the original list element! (Unless the list was not convertable)
		* 
		* Example:
		*   $('#menu').jqDock({align:'top'});
		* 
		* If you use jqDock on lists, I would suggest filtering the returned collection for successfully
		* initialised Docks, eg ...
		*   $('ul.menus').jqDock({align:'top'}).filter('.jqDocked);
		* 
		* Alternatively, pre-convert any lists...
		*   $('ul.menus').map(function(){
		*       var conv = $.jqdock.convert(this);
		*       return conv === this ? null : conv;
		*     })
		*     .jqDock({align:'top'});
		***************************************************************************************************/
		}else{ //standard usage...
			//note : returning the map() instead of returning 'this'
			rtn = rtn
				.map(CONVERT_LISTS)
				.filter(function(){
					//check that this is not already docked or part of a dock, that it has children, and that all
					//those children are either images, or anchors containing only an image...
					var kids = $(this).children();
					return !MENU_OR_ITEM(this).length && kids.length && !kids.not('img').filter(function(){
							//returns true for everything EXCEPT an anchor containing just 1 image...
							return $(this).children('img').parent('a').children().length !== 1;
						}).length;
				})
				.each(function(){
					var Menu = $(this).addClass(CLASSES[0]),
							images = $('img', Menu),
							Dock = {
								Menu : Menu, //original menu element (as jQuery object)
								Id : CLASSES[8] + $.jqdock.nextId++, //needed to give each Dock a unique id, eg. div#jqDockN.jqDock
								Elem : [], //an object per img menu option
								OnDock : 0, //indicates cursor over menu and initial sizes set
								Stamp : 0, //set on mouseenter/leave and used (within opts.duration) to proportion the menu element sizes
								width : 0, //width of div.jqDock container
								height : 0, //height of div.jqDock container
								Spread : 0, //main axis dimension (horizontal = width, vertical = height)
								Border : [], //border widths on div.jqDock, indexed as per TRBL
								Opts : $.extend({},           //options...
									$.jqdock.defaults,            //defaults as base
									Menu.data('jqdock'),          //eg. data-jqdock='{"option1":value,"option2":"value"}'
									NAMESPACED_DATA(Menu.data()), //eg. data-jqdock-option1='value' data-jqdock-option2='value'
									opts || {}                    //supplied params (highest priority)
								),
								Current : -1, //current image index
								Load : images.length, //count of images to load
								ElCt : images.length, //length of Elem array
								ToFro : [ //a pain, but needed to prevent possible oscillation around a stationary cursor on the dock (see SET_SIZES)...
									'', //previous-but-one, held as ... [ XY dimension, Dock.Current, newFinal dimension ].join(',')
									'', //previous, held as ... [ XY dimension, Dock.Current, newFinal dimension ].join(',')
									0  //previous newFinal dimension
									],
								Style : Menu.attr('style') //used by 'destroy' to reset original element's style
/* these don't need to be explicitly set a this stage, either because their usage is by testing for [non]existence and
 * then assigning a value, or because they are explicitly set during initDock()...
								Inactive : null //inactivity timer
								Idler : null //idle timer
								Indock : null //timer for IN_DOCK recursion
								Overdock : null //timer for OVER_DOCK recursion
								Offdock : null //timer for OFF_DOCK recursion
								Asleep : false //set to true when dock is put is to sleep following an idle period timeout
								Frozen : false //set to true when the dock is 'freeze'd (dock is also sent to sleep!)
								Doze : null //while Asleep, the most recent mouse event gets buffered for use on being nudged awake
								Yard : 0 //jQuery of div.jqDock
*/
							},
							op = Dock.Opts, //convenience
							mc, i;
					//check some of the options...
					for(i in {size:1, distance:1, duration:1, inactivity:1, fadeIn:1, step:1, idle:1, active:1, sizeMax:1}){
						op[i] = AS_INTEGER(op[i]);
					}
					i = ONE * op.coefficient;
					op.coefficient = isNaN(i) ? 1.5 : i;
					//check bias, and reset align if need be...
					mc = {middle:1, center:2}[op.align];
					if(mc){
						i = AS_INTEGER(op.bias);
						if(i < 1){
							//zero (or less) bias : if align=middle set to top, else (=center) set to left...
							op.align = mc < 2 ? 'top' : 'left';
						}
						if(i > 99){
							//100 (or more) bias : if align=middle set to bottom, else (=center) set to right...
							op.align = mc < 2 ? 'bottom' : 'right';
						}
						op.bias = i;
					}
					if(op.labels && !(/^[tmb][lcr]$/).test(op.labels.toString())){
						op.labels = {top:'br',left:'tr'}[op.align] || 'tl';
					}
					if(!op.setLabel){
						op.setLabel = TRANSFORM_LABEL;
					}
					op.fadeLayer = op.fadeIn ? (({dock:1,wrap:1}[op.fadeLayer]) ? op.fadeLayer : 'menu') : '';
					for(i in {onSleep:1, onWake:1, onReady:1}){
						if(!op[i]){
							op[i] = op[i] === false ? FALSEFUNC : $.noop; //jquery v1.4+
						}
					}
					mc = (/^m|c$/).test(op.labels); //indicates the need for middle/centre label positioning information to be gathered
					//set up some extra Opts now, just to save some computing power later...
					op.attenuation = Math.pow(op.distance, op.coefficient); //straightforward, static calculation
					op.vh = ({left:1, center:1, right:1}[op.align]) ? 'v' : 'h'; //vertical/horizontal orientation based on 'align' option

					Menu.data(DATA_NAMES[0], Dock);

					images.each(function(n, el){
							//add an object to the dock's elements array for each image...
							var jself = $(el),
									linkParent = jself.parent('a'),
									origAnchorTitle = linkParent.attr('title') || '',
									origImg = {},
									i, pre, altsrc;
							for(i in {src:1, alt:1, title:1, style:1}){
								origImg[i] = jself.attr(i) || '';
							}
							//image path for large image comes from the 'source' option (if provided), or
							//a data-jqdock-altsrc attribute, or the 'alt' attribute, or is the same as the 'src'...
							altsrc = (op.source ? op.source.call(el, n) : '')  //image path, large
									|| (jself.data('jqdockAltsrc') || '')
									|| ((/\.(gif|jpg|jpeg|png)$/i).test(origImg.alt || '') ? origImg.alt : '')
									|| origImg.src;
							Dock.Elem[n] = { 
								Img : jself, //jQuery of img element
								src : origImg.src,  //image path, small
								altsrc : altsrc,  //image path, large
								Title : origImg.title || origAnchorTitle || '', //label text? (pre setLabel())
								Orig : {
									i : $.extend({}, origImg),
									a : {title: origAnchorTitle, style:linkParent.attr('style') || ''}
								},
								Label : {
									mc: mc //if set, it gets removed the first time this label gets positioned
/* these don't need to be explicitly set at this stage: the first one is *always* set in init(); the other 2 are
 * only set (and used) when positioning the labels *if* the labels are being positioned middle and/or center
									el: 0 //jqQuery of div.jqDockLabel
									v: 0 //the 'v' stands for vertical, so this is the label's overall height (ie. height + top/bottom padding)
									h: 0 //the 'h' stands for horizontal, so this is the label's overall width (ie. width + left/right padding)
 */
								},
								Pad : [], //user-applied padding, set up below and indexed as per TRBL
								Link : linkParent.length ? 'Link' : 'Image' //image-within-link or not
/* these don't need to be explicitly set a this stage, either because their usage is by testing for [non]existence and
 * then assigning a value, or because they are explicitly set during IMAGE_ONLOAD() or initDock()...
								width : 0 //original width of img element (the one that expands)
								height : 0 //original height of img element (the one that expands)
								Initial : 0 //width/height when fully shrunk; it's important to note that this is not the same as Opts.size because it's a *major* axis dimension!
								Major : 0 //transitory width/height (main axis)
								Minor : 0 //transitory width/height (minor axis)
								Final : 0 //target width/height
								Offset : 0 //offset of 'lead' edge of the image within div.jqDock (including user-padding)
								Centre : 0 //'Offset' + 'lead' user-padding + half 'Initial' dimension
								Wrap : 0 //jQuery of the menu element's immediate parent wrapper
								Item : 0 //jQuery of the menu element's outer wrapper, div.jqDockItem
*/
								};
							i = 4;
							while(i--){
								Dock.Elem[n].Pad[i] = AS_INTEGER(jself.css('padding' + TRBL[i]));
							}
							//we have to run a 'loader' function for the images because the expanding image
							//may not be part of the current DOM. what this means though, is that if you
							//have a missing image in your dock, the entire dock will not be displayed!
							//however I've had a few problems with certain browsers: for instance, IE does
							//not like the jQuery method; and Opera was causing me problems with the native
							//method when reloading the page; I've also heard rumours that Safari 2 might cope better with
							//the jQuery method, but I cannot confirm since I no longer have Safari 2.
							//
							//anyway, I'm providing both methods. if anyone finds it doesn't work, try
							//overriding with option.loader, and/or changing $.jqdock.useJqLoader for the 
							//browser in question and let me know if that solves it.
							if(op.loader ? op.loader === 'jquery' : $.jqdock.useJqLoader){ //jQuery method...
								$('<img>').on('load', {idx:n, menu:Menu[0]}, IMAGE_ONLOAD).attr({src:altsrc});
							}else{ //native 'new Image()' method...
								pre = new Image();
								pre.onload = function(){
										IMAGE_ONLOAD.call(this, {data:{idx:n, menu:Menu[0]}});
										pre.onload = ''; //wipe out this onload function
										pre = null;
									};
								pre.src = altsrc;
							}
						});
				}).end(); //remove the filter(), but not the map()!
		}
		return rtn;
	}; //end jQuery.fn.jqDock()
} //end of if()

//v2.0 : a class of jqDockAuto on an element will auto-run jqdock on document ready...
$(function(){
	$('.jqDockAuto').jqdock();
});

}(jQuery, window));
