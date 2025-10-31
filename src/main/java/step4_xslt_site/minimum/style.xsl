<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!-- Feuille XSLT avec pagination simplifiée pour la structure minimum -->
  <xsl:output method="html" encoding="UTF-8" indent="yes"/>
  
  <!-- Nombre d'éléments par page -->
  <xsl:variable name="itemsPerPage" select="50"/>
  
  <xsl:template match="/images">
    <html>
      <head>
        <meta charset="UTF-8"/>
        <title>PadChest – Liste des images (avec pagination)</title>
        <style>
          body { font-family: Arial, sans-serif; margin: 20px; }
          table { border-collapse: collapse; width: 100%; }
          th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
          th { background-color: #f0f0f0; }
          tr:nth-child(even) { background-color: #fafafa; }
          
          /* Styles pour la pagination */
          .pagination { margin: 20px 0; text-align: center; }
          .pagination button { 
            margin: 0 5px; 
            padding: 8px 12px; 
            border: 1px solid #ccc; 
            background: #f9f9f9; 
            cursor: pointer; 
          }
          .pagination button:hover { background: #e9e9e9; }
          .pagination button.active { background: #007cba; color: white; }
          .pagination button:disabled { background: #f0f0f0; color: #ccc; cursor: not-allowed; }
          
          .page-info { margin: 10px 0; text-align: center; color: #666; }
          
          /* Masquer toutes les lignes par défaut */
          .image-row { display: none; }
          
          /* Classe pour afficher les lignes */
          .image-row.visible { display: table-row; }
        </style>
      </head>
      <body>
        <h1>Images radiographiques</h1>
        
        <!-- Informations sur la page -->
        <div class="page-info">
          <span id="pageInfo">Page 1 sur <span id="totalPages">1</span></span>
        </div>
        
        <!-- Contrôles de pagination -->
        <div class="pagination">
          <button id="firstPage" onclick="goToPage(1)">Premier</button>
          <button id="prevPage" onclick="previousPage()">Précédent</button>
          <span id="pageNumbers"></span>
          <button id="nextPage" onclick="nextPage()">Suivant</button>
          <button id="lastPage" onclick="goToPage(totalPages)">Dernier</button>
        </div>
        
        <table>
          <tr>
            <th>ID</th>
            <th>Patient</th>
            <th>Vue</th>
            <th>Labels</th>
            <th>Localisations</th>
          </tr>
          <xsl:for-each select="image">
            <tr class="image-row" data-page="{(position() - 1) idiv $itemsPerPage + 1}">
              <td><xsl:value-of select="@id"/></td>
              <td><xsl:value-of select="patientId"/></td>
              <td><xsl:value-of select="viewPosition"/></td>
              <td>
                <xsl:for-each select="labels/label">
                  <xsl:value-of select="."/>
                  <xsl:if test="position() != last()">, </xsl:if>
                </xsl:for-each>
                <xsl:if test="not(labels/label)">-</xsl:if>
              </td>
              <td>
                <xsl:for-each select="localizations/localization">
                  <xsl:value-of select="."/>
                  <xsl:if test="position() != last()">, </xsl:if>
                </xsl:for-each>
                <xsl:if test="not(localizations/localization)">-</xsl:if>
              </td>
            </tr>
          </xsl:for-each>
        </table>
        
        <!-- Pagination en bas -->
        <div class="pagination">
          <button id="firstPageBottom" onclick="goToPage(1)">Premier</button>
          <button id="prevPageBottom" onclick="previousPage()">Précédent</button>
          <span id="pageNumbersBottom"></span>
          <button id="nextPageBottom" onclick="nextPage()">Suivant</button>
          <button id="lastPageBottom" onclick="goToPage(totalPages)">Dernier</button>
        </div>
        
        <script>
        <![CDATA[
          let currentPage = 1;
          let totalPages = 1;
          const itemsPerPage = ]]><xsl:value-of select="$itemsPerPage"/><![CDATA[;
          
          // Calculer le nombre total de pages
          function calculateTotalPages() {
            const totalItems = document.querySelectorAll('.image-row').length;
            totalPages = Math.ceil(totalItems / itemsPerPage);
            document.getElementById('totalPages').textContent = totalPages;
            console.log('Total items:', totalItems, 'Total pages:', totalPages);
          }
          
          // Afficher une page spécifique
          function showPage(page) {
            console.log('Showing page:', page);
            
            // Masquer toutes les lignes
            document.querySelectorAll('.image-row').forEach(row => {
              row.classList.remove('visible');
            });
            
            // Afficher les lignes de la page courante
            const rowsToShow = document.querySelectorAll('.image-row[data-page="' + page + '"]');
            console.log('Rows to show for page', page, ':', rowsToShow.length);
            
            rowsToShow.forEach(row => {
              row.classList.add('visible');
            });
            
            // Mettre à jour les informations de page
            document.getElementById('pageInfo').textContent = 'Page ' + page + ' sur ' + totalPages;
            
            // Mettre à jour les boutons
            updatePaginationButtons();
          }
          
          // Mettre à jour les boutons de pagination
          function updatePaginationButtons() {
            // Boutons Premier/Précédent
            document.getElementById('firstPage').disabled = currentPage === 1;
            document.getElementById('firstPageBottom').disabled = currentPage === 1;
            document.getElementById('prevPage').disabled = currentPage === 1;
            document.getElementById('prevPageBottom').disabled = currentPage === 1;
            
            // Boutons Suivant/Dernier
            document.getElementById('nextPage').disabled = currentPage === totalPages;
            document.getElementById('nextPageBottom').disabled = currentPage === totalPages;
            document.getElementById('lastPage').disabled = currentPage === totalPages;
            document.getElementById('lastPageBottom').disabled = currentPage === totalPages;
            
            // Numéros de page
            updatePageNumbers();
          }
          
          // Mettre à jour les numéros de page
          function updatePageNumbers() {
            const pageNumbers = document.getElementById('pageNumbers');
            const pageNumbersBottom = document.getElementById('pageNumbersBottom');
            
            let startPage = Math.max(1, currentPage - 2);
            let endPage = Math.min(totalPages, currentPage + 2);
            
            let html = '';
            for (let i = startPage; i <= endPage; i++) {
              html += '<button onclick="goToPage(' + i + ')"' + (i === currentPage ? ' class="active"' : '') + '>' + i + '</button>';
            }
            
            pageNumbers.innerHTML = html;
            pageNumbersBottom.innerHTML = html;
          }
          
          // Aller à une page spécifique
          function goToPage(page) {
            if (page >= 1 && page <= totalPages) {
              currentPage = page;
              showPage(currentPage);
            }
          }
          
          // Page précédente
          function previousPage() {
            if (currentPage > 1) {
              goToPage(currentPage - 1);
            }
          }
          
          // Page suivante
          function nextPage() {
            if (currentPage < totalPages) {
              goToPage(currentPage + 1);
            }
          }
          
          // Initialisation
          document.addEventListener('DOMContentLoaded', function() {
            console.log('DOM loaded, initializing pagination...');
            calculateTotalPages();
            showPage(1);
          });
        ]]>
        </script>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>