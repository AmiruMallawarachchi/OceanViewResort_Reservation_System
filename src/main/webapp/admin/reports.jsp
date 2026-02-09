<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.oceanview.resort.dto.ReportDTO" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Reports</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Management Reports</h1>
        <p class="muted">Generate operational and revenue summaries.</p>
      </div>
    </div>

    <%@ include file="/WEB-INF/partials/flash.jspf" %>

    <div class="panel">
      <form class="form" method="post" action="<%= request.getContextPath() %>/reports" style="grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));">
        <div class="form__group">
          <label>Report Type</label>
          <select name="reportType">
            <option value="OCCUPANCY">Occupancy</option>
            <option value="REVENUE">Revenue</option>
            <option value="RESERVATION">Reservation</option>
          </select>
        </div>
        <div class="form__group">
          <label>Period</label>
          <select name="period">
            <option value="DAILY">Daily</option>
            <option value="WEEKLY">Weekly</option>
            <option value="MONTHLY">Monthly</option>
            <option value="CUSTOM">Custom</option>
          </select>
        </div>
        <div class="form__group">
          <label>Start Date</label>
          <input type="date" name="startDate" />
        </div>
        <div class="form__group">
          <label>End Date</label>
          <input type="date" name="endDate" />
        </div>
        <div class="form__group">
          <label>Format</label>
          <select name="format">
            <option value="PDF">PDF</option>
            <option value="EXCEL">Excel</option>
          </select>
        </div>
        <div class="form__group" style="align-self: end;">
          <button class="btn btn--primary" type="submit">Generate</button>
        </div>
      </form>
    </div>

    <div class="section panel">
      <h2>Generated Reports</h2>
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Type</th>
            <th>Period</th>
            <th>Format</th>
            <th>Generated</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <%
            List<ReportDTO> reports = (List<ReportDTO>) request.getAttribute("reports");
            if (reports != null && !reports.isEmpty()) {
              for (ReportDTO report : reports) {
          %>
          <tr>
            <td><%= report.getId() %></td>
            <td><%= report.getReportType() %></td>
            <td><%= report.getParameters() == null ? "-" : report.getParameters() %></td>
            <td><%= report.getFormat() %></td>
            <td><%= report.getGeneratedAt() %></td>
            <td>
              <a class="btn btn--outline btn--sm" href="<%= request.getContextPath() %>/reports?downloadId=<%= report.getId() %>&view=1">View</a>
              <a class="btn btn--outline btn--sm" href="<%= request.getContextPath() %>/reports?downloadId=<%= report.getId() %>">Download</a>
            </td>
          </tr>
          <%
              }
            } else {
          %>
          <tr>
            <td colspan="6">No reports generated yet.</td>
          </tr>
          <%
            }
          %>
        </tbody>
      </table>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>

