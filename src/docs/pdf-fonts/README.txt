1.Download fop from xml.apache.org/fop
unarchive and add the 2 scripts from fop_scripts to the main directory
These scripts will allow you to process your own font.

2. To create your own font mappings, use
ttfreader.bat (or .sh)  path/fontfile.ttf xmlfile.xml

3. Copy both the ttf font and the generated xml to the WEB-INF/jpivot/print
directory of your web_app

4. Edit userconfig.xml. Change the following line to the fontfile.ttf and xmlfile.xml
that you generated before
<font metrics-file="arial.xml" embed-file="arial.ttf" kerning="yes">

5. recompile and re-war your webapp and you should be ready to go.
Please let me know of any mistakes/problems.

Cheers
Ati

2004.10.19 @ Budapest, Hungary

2006: arial.ttf removed for copyright reasons