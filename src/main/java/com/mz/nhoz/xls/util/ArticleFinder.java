package com.mz.nhoz.xls.util;

import com.mz.nhoz.xls.ExcelReader;
import com.mz.nhoz.xls.ExcelRecord;
import com.mz.nhoz.xls.util.exception.ArticleFinderException;
import com.mz.nhoz.xls.util.exception.CellParserException;

/**
 * Buscador de articulos.
 * 
 * @author martin
 *
 */
public class ArticleFinder {
	private ExcelReader reader;

	public ArticleFinder(ExcelReader reader) {
		super();
		this.reader = reader;
	}

	/**
	 * Busca un articulo a partir de un id.
	 * 
	 * @param articleId
	 *            Id de articulo a buscar.
	 * @return Articulo encontrado. Si el articulo no se encuentra,
	 *         {@link ExcelRecord#newNullRecord()}.
	 * @see ExcelRecord#isNull()
	 * @throws ArticleFinderException
	 *             En caso que no se pueda obtener los valores de algun
	 *             registro.
	 */
	public ExcelRecord find(String articleId) throws ArticleFinderException {
		int articleIndex = 0;

		for (ExcelRecord excelRecord : reader) {
			Object cellValue;
			try {
				cellValue = excelRecord.getCellValue(0);
			} catch (CellParserException e) {
				throw new ArticleFinderException("Error al obtener el codigo de articulo " + articleIndex);
			}
			if (articleId.equals(cellValue)) {
				return excelRecord;
			}
		}

		return ExcelRecord.newNullRecord();
	}
}
