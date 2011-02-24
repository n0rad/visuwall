jwall.view.project = {

		
		changeColor : function() {
			 b.hover(function() {
				$(this).animate({ backgroundColor: "#00aadd"});
			 });
		},
		
		buildProject : function(project, htmlElement) {
			LOG.info("display project", project);
			var projectTR = $('<tr></tr>');
			var projectTD = $('<td class="project success"></td>');
			projectTR.append(projectTD);
			projectTD.append('<p class="projectName">' + project.name + '<span id="when">(1d)</span></p>');
			
			
			
			
			var metrics = $('<ul class="projectMetrics"></ul>');

			metrics.append($('<li>coverage : ' + project.coverage + '</li>'));
			metrics.append($('<li>rulesCompliance : ' + project.rulesCompliance + '</li>'));
			metrics.append($('<li>failCount : ' + project.hudsonProject.lastBuild.testResult.failCount + '</li>'));
			metrics.append($('<li>skipCount : ' + project.hudsonProject.lastBuild.testResult.skipCount + '</li>'));
			metrics.append($('<li>totalCount : ' + project.hudsonProject.lastBuild.testResult.totalCount + '</li>'));
			metrics.append($('<li>passCount : ' + project.hudsonProject.lastBuild.testResult.passCount + '</li>'));
			metrics.append($('<li>integrationTestCount : ' + project.hudsonProject.lastBuild.testResult.integrationTestCount + '</li>'));
			metrics.sortable();
			
			
			projectTD.append(metrics);
			
			
			$('#projectsTable').append(projectTR);
			
		}

//
// function effectFadeIn(classname) {
// $("."+classname).fadeTo(3000, 0.4, effectFadeOut(classname));
// }
// function effectFadeOut(classname) {
// $("."+classname).fadeTo(3000, 1, effectFadeIn(classname));
// }
// $(document).ready(function(){
// effectFadeIn('testt');
// });

};