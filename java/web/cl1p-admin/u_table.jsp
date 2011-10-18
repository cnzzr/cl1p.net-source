
Account Status
<table>
<tr><td>
<p>You currently have a BASIC account</p>
<p><b>Upgrade to a PRO account</b></p>
<p>More features then a basic account and
all ads are removed.</p>
Additional features<br>
<ul>
<li>Upload files up to 100MB</li>
<li>Create permanent cl1ps, own the URLS you create</li>
<li>Use SSL to securely access and edit your cl1ps</li>
<li>No advertisements</li>
</ul>
Only $19.95 a year.

<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_xclick">
<input type="hidden" name="business" value="cl1p@cl1p.net">
<input type="hidden" name="item_name" value="CL1P 1 Year Pro">
<input type="hidden" name="item_number" value="CL1P_1YEAR">
<input type="hidden" name="amount" value="19.95">
<input type="hidden" name="no_shipping" value="2">
<input type="hidden" name="no_note" value="1">
<input type="hidden" name="currency_code" value="USD">
<input type="hidden" name="lc" value="CA">
<input type="hidden" name="bn" value="PP-BuyNowBF">
<input type="hidden" name="custom" value="<%= clipSession.getUser().getNumber() %>">
<input type="image" src="https://www.paypal.com/en_US/i/btn/x-click-butcc.gif" border="0" name="submit" alt="Make payments with PayPal - it's fast, free and secure!">
<img alt="" border="0" src="https://www.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1">
</form>

<!--<form action="https://www.paypal.com/cgi-bin/webscr" method="post">
<input type="hidden" name="cmd" value="_xclick">
<input type="hidden" name="business" value="cl1p@cl1p.net">
<input type="hidden" name="item_name" value="cl1p 1 year subscription">
<input type="hidden" name="item_number" value="cl1p-1y">
<input type="hidden" name="amount" value="19.95">
<input type="hidden" name="no_shipping" value="2">
<input type="hidden" name="return" value="http://javajax.org/cl1p-admin/paypal.jsp">
<input type="hidden" name="no_note" value="1">
<input type="hidden" name="currency_code" value="USD">
<input type="hidden" name="lc" value="US">
<input type="hidden" name="bn" value="PP-BuyNowBF">
<input type="hidden" name="custom" value="<%= clipSession.getUser().getNumber() %>">
<input type="submit" value="Upgrade, for $19.95 a year!"/>

<img alt="" border="0" src="https://www.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1">
</form>-->

