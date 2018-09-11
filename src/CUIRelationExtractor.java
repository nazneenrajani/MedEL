import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeSet;

import com.ibm.watsonmd.tools.umls.ConceptBrowserAPI.UMLSConceptBrowserAPI;
import com.ibm.watsonmd.tools.umls.*;


public class CUIRelationExtractor {

	private static final String OUTFILE = "/users/nfrajani/UMLS/rel.tsv";
	private static final String FILEHEADER = "<?xml version=\"1.0\"?>\n";
	private static final String TLEHEADER = "<DOCS>\n";
	private static final String TLETRAILER = "</DOCS>\n";

	private static final String DOCHEADER = "<DOC id=\"";
	private static final String DOCHEADERTAIL = "\">\n";
	private static final String DOCTRAILER = "</DOC>\n";

	private static final String TYPESHEADER = "  <TYPES>\n";
	private static final String TYPESTRAILER = "  </TYPES>\n";
	private static final String TYPEHEADER = "    <TYPE>";
	private static final String TYPETRAILER = "</TYPE>\n";

	private static final String VARIANTSHEADER = "  <VARIANTS>\n";
	private static final String VARIANTSTRAILER = "  </VARIANTS>\n";
	private static final String VARIANTHEADER = "    <VARIANT>";
	private static final String VARIANTTRAILER = "</VARIANT>\n";

	private static final String RELSHEADER = "  <RELS>\n";
	private static final String RELSTRAILER = "  </RELS>\n";
	private static final String RELHEADER = "    <REL>\n";
	private static final String RELTRAILER = "    </REL>\n";
	private static final String RELTYPEHEADER = "      <RELTYPE>";
	private static final String RELTYPETRAILER = "</RELTYPE>\n";
	private static final String RELARGHEADER = "      <RELARG>";
	private static final String RELARGTRAILER = "</RELARG>\n";

	private static final String DEFSHEADER = "  <DEFS>\n";
	private static final String DEFSTRAILER = "  </DEFS>\n";
	private static final String DEFHEADER = "    <DEF>\n";
	private static final String DEFTRAILER = "    </DEF>\n";
	private static final String DEFSOURCEHEADER = "      <DEFSOURCE>";
	private static final String DEFSOURCETRAILER = "</DEFSOURCE>\n";
	private static final String DEFTEXTHEADER = "      <DEFTEXT>";
	private static final String DEFTEXTTRAILER = "</DEFTEXT>\n";
	private int rela;
	private String rel;
	private Object cui2;
	private Object cui1;

	public static void main(String[] args) throws IOException {
		int cuiLimit = ConceptBrowserAPI.getCUINumberLimit();
		TreeSet<String> defs = null;
		TreeSet<String> types = null;
		TreeSet<UMLSCUIVariant> ucvs = null;
		TreeSet<CUIRelation> crs = null;

		BufferedWriter of = new BufferedWriter(new FileWriter(OUTFILE));
		//of.write(FILEHEADER);
		//of.write(TLEHEADER);

		for(int i = 0; i <= cuiLimit; i++) {
			String cui = String.format("C%07d", i);

			types = ConceptBrowserAPI.cui2sty(cui);
			if(types != null) {
				/*				of.write(DOCHEADER + cui + DOCHEADERTAIL);

					 dump all types for the CUI 
					of.write(TYPESHEADER);
					for(String tui : types) {
						String typeName = ConceptBrowserAPI.tui2name(tui);
						if(typeName != null)
							of.write(TYPEHEADER + typeName + TYPETRAILER);
					}
					of.write(TYPESTRAILER);

					 dump all definitions for the CUI 
					defs = ConceptBrowserAPI.cui2SabAndDef(cui);
					if(defs != null) {
						of.write(DEFSHEADER);
						for(String def : defs) {
							String[] defparts = def.split("\\|");
							if(defparts[1] != null) {
								of.write(DEFHEADER);
								of.write(DEFSOURCEHEADER + defparts[0] + DEFSOURCETRAILER);
								of.write(DEFTEXTHEADER + cleanString(defparts[1]) + DEFTEXTTRAILER);
								of.write(DEFTRAILER);
							}
						}
						of.write(DEFSTRAILER);
					}	*/			

				/* dump all variants for the CUI */
				/*				ucvs = UMLSConceptBrowserAPI.cui2UMLSCUIVariant(cui);
					if(ucvs != null) {
						of.write(VARIANTSHEADER);
						for(UMLSCUIVariant ucv : ucvs) {
							of.write(VARIANTHEADER + cleanString(ucv.variant) + VARIANTTRAILER);
						}
						of.write(VARIANTSTRAILER);
					}*/

				/* dump all rels for the CUI */
				crs = ConceptBrowserAPI.cui2CUIRelation(cui);
				if(crs != null) {
					//of.write(RELSHEADER);
					for(CUIRelation cr : crs) {
						if(cr.cui1.equals(cr.cui2))
							continue;

						String compositeRel = cr.rel + ":" + cr.rela;
						if(relIsBad(compositeRel))
							continue;
						
						of.write(cr.cui1+"\t"+compositeRel+"\t"+cr.cui2+"\n");
						//of.write(RELHEADER);
						//of.write(RELTYPEHEADER + compositeRel + RELTYPETRAILER);
						//of.write(RELARGHEADER + cr.cui2 + RELARGTRAILER);
						//of.write(RELTRAILER);
					}
					//of.write(RELSTRAILER);
				}

				//of.write(DOCTRAILER);
			}
		}
		//of.write(TLETRAILER);
		of.close();
	}
	private static String cleanString(String inStr) {
		String retStr = stripHtmlTags(inStr);
		retStr = deHtmlEntities(retStr);
		retStr = reHtmlEntities(retStr);
		retStr = stripUnprintableChars(retStr);

		return retStr;
	}

	private static String stripHtmlTags(String inStr) {
		String retStr = inStr.replaceAll("<[^<>]*>", " ");

		return retStr;
	}

	private static String deHtmlEntities(String inStr) {
		String retStr = inStr.replaceAll("&lsquo;", "'");
		retStr = retStr.replaceAll("&rsquo;", "'");
		retStr = retStr.replaceAll("&ldquo;", "\"");
		retStr = retStr.replaceAll("&rdquo;", "\"");
		retStr = retStr.replaceAll("&amp;", "&");
		retStr = retStr.replaceAll("&nbsp;", " ");
		retStr = retStr.replaceAll("&lt;", "<");
		retStr = retStr.replaceAll("&gt;", ">");

		return retStr;
	}

	private static String reHtmlEntities(String inStr) {
		String retStr = inStr.replaceAll("&", "&amp;");
		retStr = retStr.replaceAll("<", "&lt;");
		retStr = retStr.replaceAll(">", "&gt;");
		return retStr;
	}

	private static String stripUnprintableChars(String inStr) {
		String retStr = inStr.replaceAll("[\000-\037]", "");
		return retStr;
	}

	private static String[] BADRELS = {
		//		  ".+:nil",
		".+:has_mapping_qualifier", ".+:mapping_qualifier_of",
		//		  ".+:has_episodicity", ".+:episodicity_of",
		".+:has_fragments_for_synonyms", ".+:fragments_for_synonyms_of",
		".+:has_revision_status", ".+:revision_status_of",
		//		  ".+:has_severity", ".+:severity_of",
		".+:has_priority", ".+:priority_of",
		".+:has_suffix", ".+:suffix_of",
		".+:classifies", ".+:classified_as",
		".+:has_british_form", ".+:british_form_of",
		".+:has_expanded_form", ".+:expanded_form_of",
		".+:has_permuted_form", ".+:permuted_form_of",
		".+:has_translation", ".+:translation_of",
		//		  ".+:has_clinical_course", ".+:clinical_course_of",
		".+:has_fragments_for_synonyms", ".+:fragments_for_synonyms_of",
		".+:mth_has_plain_text_form", ".+:mth_plain_text_form_of",
		".+:has_entry_version", ".+:entry_version_of",
		".+:has_access", ".+:access_of"
	};

	private static boolean relIsBad(String compositeRel) {
		for(String br : BADRELS)
			if(compositeRel.matches(br))
				return true;

		return false;
	}

}
