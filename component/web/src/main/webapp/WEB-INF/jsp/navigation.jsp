<%--

    Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>

<%@ page language="java" contentType="text/html; charset=utf-8"%>
<div id="navigationContainer">
	<div id="modal" style="display: none"></div>
	<ul id="navigation">
		<li id="name">Visuwall</li>
		<li id="wallSelector">
			<select id="wallSelect" name="wall"></select>
			<button id="edit" type="button" value="Edit">Edit</button>
			<button id="add" type="button" value="Add">Add</button>
		</li>
		<li><div id="fontSizeSlider"></div></li>
		<li><button id="refresh">Refresh</button></li>
		<li id="helper"><img id="helperimg" src="res/img/helpS.png"></li>
		<li id="about"><a href="http://visuwall.awired.net/"
			title="Visuwall">${version}</a></li>
	</ul>
</div>