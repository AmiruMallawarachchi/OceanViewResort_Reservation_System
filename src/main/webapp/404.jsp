<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Page Not Found | OceanView Resort</title>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css?v=20260213" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container" style="text-align: center; padding: 48px 24px;">
    <h1 class="page-header__title" style="font-size: 4rem; margin-bottom: 16px;">404</h1>
    <p class="muted" style="font-size: 1.25rem; margin-bottom: 24px;">The page you requested could not be found.</p>
    <a href="<%= ctx %>/index.jsp" class="btn btn--primary">Go to Home</a>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
