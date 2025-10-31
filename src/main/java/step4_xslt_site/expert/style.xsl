<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!-- Feuille XSLT Expert avec design moderne et fonctionnalités avancées -->
  <xsl:output method="html" encoding="UTF-8" indent="yes"/>
  
  <!-- Nombre d'éléments par page -->
  <xsl:variable name="itemsPerPage" select="25"/>
  
  <xsl:template match="/images">
    <html>
      <head>
        <meta charset="UTF-8"/>
        <title>PadChest Expert Dashboard - Medical Imaging Analytics</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <style>
          * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
          }
          
          body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
            background: #f5f7fa;
            min-height: 100vh;
            color: #1e293b;
            line-height: 1.6;
          }
          
          .container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 20px;
          }
          
          .header {
            background: white;
            border-radius: 8px;
            padding: 30px;
            margin-bottom: 24px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            border: 1px solid #e2e8f0;
          }
          
          .header h1 {
            font-size: 2em;
            color: #1e293b;
            margin-bottom: 8px;
            font-weight: 600;
          }
          
          .header .subtitle {
            color: #64748b;
            font-size: 1em;
            font-weight: 400;
          }
          
          .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 16px;
            margin-bottom: 24px;
          }
          
          .stat-card {
            background: white;
            border-radius: 8px;
            padding: 20px;
            text-align: center;
            border: 1px solid #e2e8f0;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
          }
          
          .stat-number {
            font-size: 2em;
            font-weight: 600;
            color: #1e293b;
            margin-bottom: 4px;
          }
          
          .stat-label {
            color: #64748b;
            font-size: 0.875em;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            font-weight: 500;
          }
          
          .controls-panel {
            background: white;
            border-radius: 8px;
            padding: 24px;
            margin-bottom: 24px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
          }
          
          .search-filter-row {
            display: flex;
            gap: 12px;
            margin-bottom: 20px;
            flex-wrap: wrap;
            align-items: center;
          }
          
          .filter-select {
            padding: 10px 16px;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            font-size: 14px;
            background: white;
            cursor: pointer;
            transition: all 0.2s ease;
          }
          
          .filter-select:focus {
            outline: none;
            border-color: #2563eb;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
          }
          
          .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 8px;
            margin: 20px 0;
            flex-wrap: wrap;
          }
          
          .pagination button {
            padding: 8px 14px;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            background: white;
            color: #475569;
            cursor: pointer;
            font-weight: 500;
            font-size: 14px;
            transition: all 0.2s ease;
          }
          
          .pagination button:hover:not(:disabled) {
            background: #f8fafc;
            border-color: #2563eb;
            color: #2563eb;
          }
          
          .pagination button:disabled {
            background: #f1f5f9;
            color: #cbd5e1;
            cursor: not-allowed;
            border-color: #e2e8f0;
          }
          
          .pagination button.active {
            background: #2563eb;
            color: white;
            border-color: #2563eb;
          }
          
          .page-info {
            background: #f8fafc;
            padding: 8px 16px;
            border-radius: 6px;
            font-weight: 500;
            color: #475569;
            font-size: 14px;
            border: 1px solid #e2e8f0;
          }
          
          .table-container {
            background: white;
            border-radius: 8px;
            padding: 24px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            overflow-x: auto;
          }
          
          table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 16px;
          }
          
          th {
            background: #f8fafc;
            color: #1e293b;
            padding: 14px 16px;
            text-align: left;
            font-weight: 600;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            cursor: pointer;
            border-bottom: 2px solid #e2e8f0;
            user-select: none;
          }
          
          th:hover {
            background: #f1f5f9;
          }
          
          th::after {
            content: '↕';
            margin-left: 8px;
            opacity: 0.5;
            font-size: 11px;
          }
          
          td {
            padding: 14px 16px;
            border-bottom: 1px solid #f1f5f9;
            font-size: 14px;
            color: #334155;
          }
          
          tr:hover td {
            background: #f8fafc;
          }
          
          .image-row {
            display: none;
          }
          
          .image-row.visible {
            display: table-row;
          }
          
          .loc-right {
            background: #fef2f2 !important;
            border-left: 3px solid #dc2626;
          }
          
          .loc-right td:first-child {
            position: relative;
            font-weight: 500;
          }
          
          .badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 4px;
            font-size: 0.75em;
            font-weight: 500;
            margin: 2px 4px 2px 0;
          }
          
          .badge-label {
            background: #dbeafe;
            color: #1e40af;
            border: 1px solid #bfdbfe;
          }
          
          .badge-location {
            background: #f3e8ff;
            color: #6b21a8;
            border: 1px solid #e9d5ff;
          }
          
          .loading {
            display: none;
            text-align: center;
            padding: 40px;
          }
          
          .spinner {
            width: 40px;
            height: 40px;
            border: 3px solid #e2e8f0;
            border-top: 3px solid #2563eb;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin: 0 auto 16px;
          }
          
          @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
          }
          
          .fade-in {
            animation: fadeIn 0.3s ease-in;
          }
          
          @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
          }
          
          .toast {
            position: fixed;
            top: 20px;
            right: 20px;
            background: #1e293b;
            color: white;
            padding: 12px 20px;
            border-radius: 6px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            transform: translateX(400px);
            transition: transform 0.3s ease;
            z-index: 1000;
            font-size: 14px;
          }
          
          .toast.show {
            transform: translateX(0);
          }
          
          @media (max-width: 768px) {
            .container {
              padding: 12px;
            }
            
            .header h1 {
              font-size: 1.5em;
            }
            
            .search-filter-row {
              flex-direction: column;
            }
            
            .pagination {
              gap: 4px;
            }
            
            .pagination button {
              padding: 6px 10px;
              font-size: 13px;
            }
          }
        </style>
      </head>
      <body>
        <div class="container">
          <!-- Header -->
          <div class="header fade-in">
            <h1>PadChest Expert Dashboard</h1>
            <p class="subtitle">Advanced Medical Imaging Analytics Platform</p>
          </div>
          
          <!-- Statistics Cards -->
          <div class="stats-grid fade-in">
            <div class="stat-card">
              <div class="stat-number" id="totalImages">0</div>
              <div class="stat-label">Total Images</div>
            </div>
            <div class="stat-card">
              <div class="stat-number" id="locRightCount">0</div>
              <div class="stat-label">Right Location</div>
            </div>
            <div class="stat-card">
              <div class="stat-number" id="uniquePatients">0</div>
              <div class="stat-label">Unique Patients</div>
            </div>
            <div class="stat-card">
              <div class="stat-number" id="currentPage">1</div>
              <div class="stat-label">Current Page</div>
            </div>
          </div>
          
          <!-- Controls Panel -->
          <div class="controls-panel fade-in">
            <div class="search-filter-row">
              <select id="viewFilter" class="filter-select">
                <option value="">All Views</option>
                <option value="POSTEROANTERIOR">Posteroanterior</option>
                <option value="LATERAL">Lateral</option>
                <option value="ANTEROPOSTERIOR">Anteroposterior</option>
              </select>
              <select id="locationFilter" class="filter-select">
                <option value="">All Locations</option>
                <option value="loc right">Right Location</option>
                <option value="loc left">Left Location</option>
              </select>
            </div>
            
            <!-- Pagination Top -->
            <div class="pagination">
              <button id="firstPage" onclick="goToPage(1)">First</button>
              <button id="prevPage" onclick="previousPage()">Previous</button>
              <span id="pageNumbers"></span>
              <button id="nextPage" onclick="nextPage()">Next</button>
              <button id="lastPage" onclick="goToPage(totalPages)">Last</button>
            </div>
            
            <div class="page-info">
              <span id="pageInfo">Page 1 of <span id="totalPages">1</span></span>
            </div>
          </div>
          
          <!-- Table Container -->
          <div class="table-container fade-in">
            <div class="loading" id="loading">
              <div class="spinner"></div>
              <p>Loading medical data...</p>
            </div>
            
            <table id="imagesTable">
          <thead>
            <tr>
                  <th onclick="sortTable(0)">Image ID</th>
                  <th onclick="sortTable(1)">Patient ID</th>
                  <th onclick="sortTable(2)">View Position</th>
                  <th onclick="sortTable(3)">Labels</th>
                  <th onclick="sortTable(4)">Localizations</th>
            </tr>
          </thead>
          <tbody>
            <xsl:for-each select="image">
              <xsl:variable name="hasLocRight" select="count(localizations/localization[.='loc right']) &gt; 0"/>
                  <tr class="image-row" data-page="1">
                    <xsl:if test="$hasLocRight"><xsl:attribute name="class">image-row loc-right</xsl:attribute></xsl:if>
                <td><xsl:value-of select="@id"/></td>
                <td><xsl:value-of select="patientId"/></td>
                <td><xsl:value-of select="viewPosition"/></td>
                <td>
                  <xsl:for-each select="labels/label">
                        <span class="badge badge-label"><xsl:value-of select="."/></span>
                  </xsl:for-each>
                      <xsl:if test="not(labels/label)">
                        <span class="badge" style="background: #f0f0f0; color: #999;">No labels</span>
                      </xsl:if>
                </td>
                <td>
                  <xsl:for-each select="localizations/localization">
                        <span class="badge badge-location"><xsl:value-of select="."/></span>
                  </xsl:for-each>
                      <xsl:if test="not(localizations/localization)">
                        <span class="badge" style="background: #f0f0f0; color: #999;">No locations</span>
                      </xsl:if>
                </td>
              </tr>
            </xsl:for-each>
          </tbody>
        </table>
            
            <!-- Pagination Bottom -->
            <div class="pagination">
              <button id="firstPageBottom" onclick="goToPage(1)">First</button>
              <button id="prevPageBottom" onclick="previousPage()">Previous</button>
              <span id="pageNumbersBottom"></span>
              <button id="nextPageBottom" onclick="nextPage()">Next</button>
              <button id="lastPageBottom" onclick="goToPage(totalPages)">Last</button>
            </div>
          </div>
        </div>
        
        <!-- Toast Notification -->
        <div id="toast" class="toast"></div>
        
        <script>
        <![CDATA[
          let currentPage = 1;
          let totalPages = 1;
          let allRows = [];
          let filteredRows = [];
          const itemsPerPage = ]]><xsl:value-of select="$itemsPerPage"/><![CDATA[;
          
          // Initialize the dashboard
          document.addEventListener('DOMContentLoaded', function() {
            console.log('Initializing PadChest Expert Dashboard...');
            
            // Show loading
            document.getElementById('loading').style.display = 'block';
            
            setTimeout(() => {
              initializeDashboard();
              document.getElementById('loading').style.display = 'none';
              showToast('Dashboard loaded successfully');
            }, 1000);
          });
          
          function initializeDashboard() {
            // Collect all rows
            allRows = Array.from(document.querySelectorAll('.image-row'));
            filteredRows = [...allRows];
            
            // Recalculate page numbers based on actual displayed rows
            recalculatePages();
            
            // Calculate statistics
            calculateStatistics();
            
            // Setup event listeners
            setupEventListeners();
            
            // Show first page
            showPage(1);
            
            console.log('Dashboard initialized successfully');
          }
          
          function calculateStatistics() {
            const totalImages = allRows.length;
            const locRightCount = allRows.filter(row => row.classList.contains('loc-right')).length;
            const uniquePatients = new Set(allRows.map(row => row.cells[1].textContent)).size;
            
            document.getElementById('totalImages').textContent = totalImages.toLocaleString();
            document.getElementById('locRightCount').textContent = locRightCount.toLocaleString();
            document.getElementById('uniquePatients').textContent = uniquePatients.toLocaleString();
            document.getElementById('currentPage').textContent = currentPage;
          }
          
          function setupEventListeners() {
            // Filter functionality
            document.getElementById('viewFilter').addEventListener('change', function() {
              filterRows();
            });
            
            document.getElementById('locationFilter').addEventListener('change', function() {
              filterRows();
            });
          }
          
          function filterRows() {
            const viewFilter = document.getElementById('viewFilter').value;
            const locationFilter = document.getElementById('locationFilter').value;
            
            filteredRows = allRows.filter(row => {
              const view = row.cells[2].textContent;
              const locations = row.cells[4].textContent.toLowerCase();
              
              const matchesView = !viewFilter || view === viewFilter;
              const matchesLocation = !locationFilter || locations.includes(locationFilter);
              
              return matchesView && matchesLocation;
            });
            
            // Recalculate pages after filtering
            recalculatePages();
            goToPage(1);
            
            showToast(`Found ${filteredRows.length} matching records`);
          }
          
          function recalculatePages() {
            const rows = filteredRows;
            rows.forEach((row, index) => {
              const pageNumber = Math.floor(index / itemsPerPage) + 1;
              row.setAttribute('data-page', pageNumber);
            });
            calculateTotalPages();
          }
          
          function calculateTotalPages() {
            const totalItems = filteredRows.length;
            totalPages = Math.ceil(totalItems / itemsPerPage);
            document.getElementById('totalPages').textContent = totalPages;
            console.log(`Total items: ${totalItems}, Total pages: ${totalPages}`);
          }
          
          function showPage(page) {
            console.log(`Showing page: ${page}`);
            
            // Hide all rows
            allRows.forEach(row => {
              row.classList.remove('visible');
            });
            
            // Show rows for current page
            const rowsToShow = filteredRows.filter(row => 
              parseInt(row.getAttribute('data-page')) === page
            );
            
            console.log(`Rows to show for page ${page}: ${rowsToShow.length}`);
            
            rowsToShow.forEach(row => {
              row.classList.add('visible');
            });
            
            // Update page info
            document.getElementById('pageInfo').textContent = `Page ${page} of ${totalPages}`;
            document.getElementById('currentPage').textContent = page;
            
            // Update pagination buttons
            updatePaginationButtons();
            
            // Add fade-in animation
            rowsToShow.forEach((row, index) => {
              setTimeout(() => {
                row.style.animation = 'fadeIn 0.3s ease-in';
              }, index * 50);
            });
          }
          
          function updatePaginationButtons() {
            // First/Previous buttons
            const firstButtons = ['firstPage', 'firstPageBottom'];
            const prevButtons = ['prevPage', 'prevPageBottom'];
            
            firstButtons.forEach(id => {
              document.getElementById(id).disabled = currentPage === 1;
            });
            
            prevButtons.forEach(id => {
              document.getElementById(id).disabled = currentPage === 1;
            });
            
            // Next/Last buttons
            const nextButtons = ['nextPage', 'nextPageBottom'];
            const lastButtons = ['lastPage', 'lastPageBottom'];
            
            nextButtons.forEach(id => {
              document.getElementById(id).disabled = currentPage === totalPages;
            });
            
            lastButtons.forEach(id => {
              document.getElementById(id).disabled = currentPage === totalPages;
            });
            
            // Update page numbers
            updatePageNumbers();
          }
          
          function updatePageNumbers() {
            const pageNumbers = document.getElementById('pageNumbers');
            const pageNumbersBottom = document.getElementById('pageNumbersBottom');
            
            let startPage = Math.max(1, currentPage - 2);
            let endPage = Math.min(totalPages, currentPage + 2);
            
            let html = '';
            for (let i = startPage; i <= endPage; i++) {
              html += `<button onclick="goToPage(${i})" ${i === currentPage ? 'class="active"' : ''}>${i}</button>`;
            }
            
            pageNumbers.innerHTML = html;
            pageNumbersBottom.innerHTML = html;
          }
          
          function goToPage(page) {
            if (page >= 1 && page <= totalPages) {
              currentPage = page;
              showPage(currentPage);
            }
          }
          
          function previousPage() {
            if (currentPage > 1) {
              goToPage(currentPage - 1);
            }
          }
          
          function nextPage() {
            if (currentPage < totalPages) {
              goToPage(currentPage + 1);
            }
          }
          
          function sortTable(columnIndex) {
            const table = document.getElementById('imagesTable');
            const isAscending = table.getAttribute('data-sort-col') != columnIndex || 
                               table.getAttribute('data-sort-dir') === 'desc';
            
            filteredRows.sort((a, b) => {
              let aVal = a.cells[columnIndex].textContent.trim();
              let bVal = b.cells[columnIndex].textContent.trim();
              
              // Handle numeric values
              if (!isNaN(parseFloat(aVal)) && !isNaN(parseFloat(bVal))) {
                aVal = parseFloat(aVal);
                bVal = parseFloat(bVal);
              } else {
                aVal = aVal.toLowerCase();
                bVal = bVal.toLowerCase();
              }
              
              if (aVal < bVal) return isAscending ? -1 : 1;
              if (aVal > bVal) return isAscending ? 1 : -1;
              return 0;
            });
            
            // Recalculate pages after sorting
            recalculatePages();
            
            // Update table attributes
            table.setAttribute('data-sort-col', columnIndex);
            table.setAttribute('data-sort-dir', isAscending ? 'asc' : 'desc');
            
            // Show first page after sorting
            goToPage(1);
            
            showToast(`Sorted by column ${columnIndex + 1} (${isAscending ? 'ascending' : 'descending'})`);
          }
          
          function showToast(message) {
            const toast = document.getElementById('toast');
            toast.textContent = message;
            toast.classList.add('show');
            
            setTimeout(() => {
              toast.classList.remove('show');
            }, 3000);
          }
          
          // Add some fun interactions
          document.addEventListener('keydown', function(e) {
            if (e.key === 'ArrowLeft' && currentPage > 1) {
              previousPage();
            } else if (e.key === 'ArrowRight' && currentPage < totalPages) {
              nextPage();
            }
          });
          
          // Add click effects to cards
          document.querySelectorAll('.stat-card').forEach(card => {
            card.addEventListener('click', function() {
              this.style.transform = 'scale(0.95)';
              setTimeout(() => {
                this.style.transform = 'scale(1)';
              }, 150);
            });
          });
        ]]>
        </script>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>