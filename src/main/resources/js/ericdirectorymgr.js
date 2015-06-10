(function($, molgenis) {
	"use strict";

	// TODO enable when update to molgenis 1.8.0 is possible
//	React.render(molgenis.ui.Table({
//		entity: 'bbmri-eric_EricSource',
//		enableInspect: false
//	}), $('#table-container')[0]);

	$(function() {
		React.render(molgenis.ui.Button({
			text : 'Download',
			style : 'primary',
			onClick : function() {
				$('#download-result-container').empty();
				$.post(molgenis.getContextUrl() + '/download').done(function(data) {
					var items = [];
					items.push('<table class="table table-striped">');
					items.push('<thead>');
					items.push('<th>Source</th><th>Status</th><th>Message</th>');
					items.push('</thead>');
					items.push('<tbody>');
					$.each(data.reports, function(i, report) {
						items.push('<tr>');
						items.push('<td>' + report.source + '</td><td>' + report.status + '</td><td>' + (report.message ? report.message : '') + '</td>');
						items.push('</tr>');
					});
					items.push('</tbody>');
					items.push('</table>');
					
					$('#download-result-container').html(items.join(''));
				});
			},
		}, 'Download'), $('#download-btn-container')[0]);
	});
	
}($, window.top.molgenis = window.top.molgenis || {}));