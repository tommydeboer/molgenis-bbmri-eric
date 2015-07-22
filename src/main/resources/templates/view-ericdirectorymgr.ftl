<#include "molgenis-header.ftl">
<#include "molgenis-footer.ftl">

<#assign css=[]>
<#assign js=["ericdirectorymgr.js"]>

<@header css js/>
<div class="row">
    <div class="col-md-offset-3 col-md-6">
        <div id="table-container">
        </div>
        <div id="download-result-container">
        </div>
        <p>Adds and/or updates the BBMRI-ERIC catalogue with data from the configured <a href="/menu/main/dataexplorer?entity=bbmri_eric_EricSource">BBMRI-ERIC sources</a>.</p> 
        <div id="download-btn-container">
        </div>
    </div>
</div>
<@footer/>