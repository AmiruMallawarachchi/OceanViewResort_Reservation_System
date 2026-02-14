<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.oceanview.resort.dto.ReportDTO" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Reports | OceanView Resort</title>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css?v=20260213" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Reports</h1>
        <p class="muted">Generate operational and revenue summaries.</p>
      </div>
    </div>

    <%@ include file="/WEB-INF/partials/flash.jspf" %>

    <div class="panel">
      <form class="form" method="post" action="<%= ctx %>/reports" style="grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));">
        <div class="form__group">
          <label>Report Type</label>
          <select name="reportType">
            <option value="RESERVATION">Daily reservations report</option>
            <option value="REVENUE">Monthly revenue report</option>
            <option value="OCCUPANCY">Room occupancy report</option>
            <option value="GUEST_SEGMENT">Guest history report</option>
            <option value="CANCELLATION">Cancellation report</option>
            <option value="DISCOUNT_PERFORMANCE">Discount performance</option>
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

    <%
      java.util.List<ReportDTO> reports = (java.util.List<ReportDTO>) request.getAttribute("reports");
      int reportCount = reports == null ? 0 : reports.size();
    %>

    <div class="section panel">
      <h2>Generated Reports</h2>
      <div class="stat-cards" style="margin-top: 8px; margin-bottom: 8px;">
        <div class="stat-card">
          <div class="stat-card__label">Total Reports</div>
          <div class="stat-card__value"><%= reportCount %></div>
          <div class="stat-card__meta">Reports generated for the selected period</div>
        </div>
      </div>
      <table class="table table--striped table--compact">
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
              <a class="btn btn--outline btn--sm" href="<%= ctx %>/reports?downloadId=<%= report.getId() %>&view=1">View</a>
              <a class="btn btn--outline btn--sm" href="<%= ctx %>/reports?downloadId=<%= report.getId() %>">Download</a>
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

