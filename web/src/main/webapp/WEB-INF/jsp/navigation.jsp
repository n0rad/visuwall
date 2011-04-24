<%@ page language="java" contentType="text/html; charset=utf-8"%>
<div id="navigationContainer">
	<div id="modal" style="display: none"></div>
	<ul id="navigation">
		<li id="name">Visuwall</li>
		<li id="wallSelector">
			<select id="wallSelect" name="wall"></select>
			<input id="edit" type="button" value="Edit">
			<input id="add" type="button" value="Add">
		</li>
		<!-- 		<li>Page :<select><option>Page 1</option>
		</select></li>
		<li>Slideshow :<input type="checkbox" /></li> -->
		<li><div id="fontSizeSlider"></div></li>
		<li><button id="refresh"></button></li>
		<li id="about"><a href="http://visuwall.awired.net/"
			title="Visuwall">${version}</a></li>
	</ul>
</div>