import java.io.*;
import java.util.*;
import java.lang.*;
class Convert
{
static String iits[] = { "a","aa","i","ii","u","uu","e","ee","ai","o","oo","au",
"q","ka","kaa","ki","kii","ku","kuu","ke","kee","kai","ko","koo","kau",
"k","nga","ngaa","ngi","ngii","ngu","nguu","nge","ngee","ngai","ngo",
"ngoo","ngau","ng","ca","caa","ci","cii","cu","cuu","ce","cee","cai",
"co","coo","cau","c","nja","njaa","nji","njii","nju","njuu","nje","njee",
"njai","njo","njoo","njau","nj",
"Ta","Taa","Ti","Tii","Tu","Tuu","Te","Tee","Tai","To","Too","Tau","T",
"Na","Naa","Ni","Nii","Nu","Nuu","Ne","Nee","Nai","No","Noo","Nau",
"N","ta","taa","ti","tii","tu","tuu","te","tee","tai","to","too","tau","t",
"nda","ndaa","ndi","ndii","ndu","nduu","nde","ndee","ndai","ndo","ndoo",
"ndau","nd",
"pa","paa","pi","pii","pu","puu","pe","pee","pai","po","poo","pau","p",
"ma","maa","mi","mii","mu","muu","me","mee","mai","mo","moo","mau","m",
"ya","yaa","yi","yii","yu","yuu","ye","yee","yai","yo","yoo","yau","y",
"ra","raa","ri","rii","ru","ruu","re","ree","rai","ro","roo","rau","r",
"la","laa","li","lii","lu","luu","le","lee","lai","lo","loo","lau","l",
"va","vaa","vi","vii","vu","vuu","ve","vee","vai","vo","voo","vau","v",
"zha","zhaa","zhi","zhii","zhu","zhuu","zhe","zhee","zhai","zho","zhoo",
"zhau","zh",
"La","Laa","Li","Lii","Lu","Luu","Le","Lee","Lai","Lo","Loo","Lau","L",
"Ra","Raa","Ri","Rii","Ru","Ruu","Re","Ree","Rai","Ro","Roo","Rau","R",
"na","naa","ni","nii","nu","nuu","ne","nee","nai","no","noo","nau","n",
"Sha","Shaa","Shi","Shii","Shu","Shuu","She","Shee","Shai","Sho","Shoo",
"Shau","Sh",
"sa","saa","si","sii","su","suu","se","see","sai","so","soo","sau","s",
"ja","jaa","ji","jii","ju","juu","je","jee","jai","jo","joo","jau","j",
"ha","haa","hi","hii","hu","huu","he","hee","hai","ho","hoo","hau","h",
"xa","xA","xi","xii","xu","xuu","xe","xee","xai","xo","xoo","xau","x",
"Sra" };

static String iitk[] = {"a","A","i","I","u","U","eV","e","E","oV","o","O","H",
"ka","kA","ki","kI","ku","kU","keV","ke","kE","koV","ko","kO","k","fa",
"fA","fi","fI","fu","fU","feV","fe","fE","foV","fo","fO","f","ca",
"cA","ci","cI","cu","cU","ceV","ce","cE","coV","co","cO","c","Fa",
"FA","Fi","FI","Fu","FU","FeV","Fe","FE","FoV","Fo","FO","F","ta",
"tA","ti","tI","tu","tU","teV","te","tE","toV","to","tO","t","Na",
"NA","Ni","NI","Nu","NU","NeV","Ne","NE","NoV","No","NO","N","wa",
"wA","wi","wI","wu","wU","weV","we","wE","woV","wo","wO","w","na",
"nA","ni","nI","nu","nU","neV","ne","nE","noV","no","nO","n","pa",
"pA","pi","pI","pu","pU","peV","pe","pE","poV","po","pO","p","ma",
"mA","mi","mI","mu","mU","meV","me","mE","moV","mo","mO","m","ya",
"yA","yi","yI","yu","yU","yeV","ye","yE","yoV","yo","yO","y","ra",
"rA","ri","rI","ru","rU","reV","re","rE","roV","ro","rO","r","la",
"lA","li","lI","lu","lU","leV","le","lE","loV","lo","lO","l","va",
"vA","vi","vI","vu","vU","veV","ve","vE","voV","vo","vO","v","lYYa",
"lYYA","lYYi","lYYI","lYYu","lYYU","lYYeV","lYYe","lYYE","lYYoV",
"lYYo","lYYO","lYY","lYa","lYA","lYi","lYI","lYu","lYU","lYeV","lYe",
"lYE","lYoV","lYo","lYO","lY","rYa","rYA","rYi","rYI","rYu","rYU","rYeV",
"rYe","rYE","rYoV","rYo","rYO","rY","nYa","nYA","nYi","nYI","nYu","nYU",
"nYeV","nYe","nYE","nYoV","nYo","nYO","nY","Ra","RA","Ri","RI","Ru","RU",
"ReV","Re","RE","RoV","Ro","RO","R","sa","sA","si","sI","su","sU","seV",
"se","sE","soV","so","sO","s","ja","jA","ji","jI","ju","jU","jeV","je",
"jE","joV","jo","jO","j","ha","hA","hi","hI","hu","hU","heV","he","hE",
"hoV","ho","hO","h","KRa","KRA","KRi","KRI","KRu","KRU","KReV","KRe",
"KRE","KRoV","KRo","KRO","KR","Sra"};

static String iscii[] = {"¤","¥","¦","§","¨","©","«","¬","­","¯","°",
"±","£",
"³","³Ú","³Û","³Ü","³Ý","³Þ","³à","³á","³â","³ä","³å","³æ",
"³è",
"·","·Ú","·Û","·Ü","·Ý","·Þ","·à","·á","·â","·ä","·å","·æ",
"·è",
"¸","¸Ú","¸Û","¸Ü","¸Ý","¸Þ","¸à","¸á","¸â","¸ä","¸å","¸æ","¸è",
"¼","¼Ú","¼Û","¼Ü","¼Ý","¼Þ","¼à","¼á","¼â","¼ä","¼å","¼æ",
"¼è",
"½","½Ú","½Û","½Ü","½Ý","½Þ","½à","½á","½â","½ä","½å","½æ","½è",
"Á","ÁÚ","ÁÛ","ÁÜ","ÁÝ","ÁÞ","Áà","Áá","Áâ","Áä",
"Áå","Áæ","Áè",
"Â","ÂÚ","ÂÛ","ÂÜ","ÂÝ","ÂÞ","Âà","Âá","Ââ","Âä","Âå","Âæ",
"Âè",
"Æ","ÆÚ","ÆÛ","ÆÜ","ÆÝ","ÆÞ","Æà","Æá","Æâ","Æä","Æå","Ææ","Æè",
"È","ÈÚ","ÈÛ","ÈÜ","ÈÝ","ÈÞ","Èà","Èá","Èâ","Èä","Èå","Èæ","Èè",
"Ì","ÌÚ","ÌÛ","ÌÜ","ÌÝ","ÌÞ","Ìà","Ìá","Ìâ","Ìä","Ìå","Ìæ",
"Ìè",
"Í","ÍÚ","ÍÛ","ÍÜ","ÍÝ","ÍÞ","Íà","Íá","Íâ","Íä","Íå","Íæ",
"Íè",
"Ï","ÏÚ","ÏÛ","ÏÜ","ÏÝ","ÏÞ","Ïà","Ïá","Ïâ","Ïä","Ïå","Ïæ","Ïè",
"Ñ","ÑÚ","ÑÛ","ÑÜ","ÑÝ","ÑÞ","Ñà","Ñá","Ñâ","Ñä","Ñå","Ñæ",
"Ñè",
"Ô","ÔÚ","ÔÛ","ÔÜ","ÔÝ","ÔÞ","Ôà","Ôá","Ôâ","Ôä","Ôå","Ôæ",
"Ôè",
"Ó","ÓÚ","ÓÛ","ÓÜ","ÓÝ","ÓÞ","Óà","Óá","Óâ","Óä","Óå","Óæ",
"Óè",
"Ò","ÒÚ","ÒÛ","ÒÜ","ÒÝ","ÒÞ","Òà","Òá","Òâ","Òä","Òå",
"Òæ","Òè",
"Ð","ÐÚ","ÐÛ","ÐÜ","ÐÝ","ÐÞ","Ðà","Ðá","Ðâ","Ðä","Ðå","Ðæ",
"Ðè",
"Ç","ÇÚ","ÇÛ","ÇÜ","ÇÝ","ÇÞ","Çà","Çá","Çâ","Çä","Çå",
"Çæ","Çè",
"Ö","ÖÚ","ÖÛ","ÖÜ","ÖÝ","ÖÞ","Öà","Öá","Öâ","Öä","Öå",
"Öæ","Öè",
"×","×Ú","×Û","×Ü","×Ý","×Þ","×à","×á","×â","×ä","×å",
"×æ","×è",
"º","ºÚ","ºÛ","ºÜ","ºÝ","ºÞ","ºà","ºá","ºâ","ºä","ºå","ºæ",
"ºè",
"Ø","ØÚ","ØÛ","ØÜ","ØÝ","ØÞ","Øà","Øá","Øâ","Øä","Øå",
"Øæ","Øè",
"³èÖ","³èÖÚ","³èÖÛ","³èÖÜ","³èÖÝ","³èÖÞ","³èÖà","³èÖá","³èÖâ",
"³èÖä","³èÖå","³èÖæ","³èÖè",
"ÕèÏ"};

static String tab[] = {"Ü","Ý","Þ","ß","à","á","â","ã","ä","å","æ","å÷","ç",
"è","è£","è¤","è¦","°","Ã","ªè","«è","¬è","ªè£","«è£","ªè÷","è¢" ,
"é","é£","é¤","é¦","±","Ä","ªé","«é","¬é","ªé£","«é£","ªé÷","é¢",
"ê","ê£","ê¤","ê¦","²","Å","ªê","«ê","¬ê","ªê£","«ê£","ªê÷","ê¢",
"ë","ë£","ë¤","ë¦","³","Æ","ªë","«ë","¬ë","ªë£","«ë£","ªë÷","ë¢",
"ì","ì£","®","¯","´","Ç","ªì","«ì","¬ì","ªì£","«ì£","ªì÷","ì¢" ,
"í","í£","í¤","í¦","µ","È","ªí","«í","¬í","ªí£","«í£","ªí÷","í¢" ,
"î","î£","î¤","î¦","¶","É","ªî","«î","¬î","ªî£","«î£","ªî÷","î¢" ,
"ï","ï£","ï¤","ï¦","¸","Ë","ªï","«ï","¬ï","ªï£","«ï£","ªï÷","ï¢",
"ð","ð£","ð¤","ð¦","¹","Ì","ªð","«ð","¬ð","ªð£","«ð£","ªð÷","ð¢",
"ñ","ñ£","ñ¤","ñ¦","º","Í","ªñ","«ñ","¬ñ","ªñ£","«ñ£","ªñ÷","ñ¢" ,
"ò","ò£","ò¤","ò¦","»","Î","ªò","«ò","¬ò","ªò£","«ò£","ªò÷","ò¢",
"ó","ó£","ó¤","ó¦","¼","Ï","ªó","«ó","¬ó","ªó£","«ó£","ªó÷","ó¢",
"ô","ô£","ô¤","ô¦","½","Ö","ªô","«ô","¬ô","ªô£","«ô£","ªô÷","ô¢" ,
"õ","õ£","õ¤","õ¦","¾","×","ªõ","«õ","¬õ","ªõ£","«õ£","ªõ÷","õ¢" ,
"ö","ö£","ö¤","ö¦","¿","Ø","ªö","«ö","¬ö","ªö£","«ö£","ªö÷","ö¢",
"÷","÷£","÷¤","÷¦","À","Ù","ª÷","«÷","¬÷","ª÷£","«÷£","ª÷÷","÷¢" ,
"ø","ø£","ø¤","ø¦","Á","Ú","ªø","«ø","¬ø","ªø£","«ø£","ªø÷","ø¢" ,
"ù","ù£","ù¤","ù¦","Â","Û","ªù","«ù","¬ù","ªù£","«ù£","ªù÷","ù¢" ,
"û","û£","û¤","û¦","û§","û¨","ªû","«û","¬û","ªû£","«û£","ªû÷","û¢" ,
"ú","ú£","ú¤","ú¦","ú§","ú¨","ªú","«ú","¬ú","ªú£","«ú£","ªú÷","ú¢",
"ü","ü£","ü¤","ü¦","ü§","ü¨","ªü","«ü","¬ü","ªü£","«ü£","ªü÷","ü¢" ,
"ý","ý£","ý¤","ý¦","ý§","ý¨","ªý","«ý","¬ý","ªý£","«ý£","ªý÷","ý¢" ,
"þ","þ£","þ¤","þ¦","þ§","þ¨","ªþ","«þ","¬þ","ªþ£","«þ£","ªþ÷","þ¢" ,
"ÿ"};



  static String iits2iitk(String text) {
    return convert(text,iits,iitk);
  }


  static String iits2iscii(String text) {
    return convert(text,iits,iscii);
  }

  static String iits2tab(String text) {
    return convert(text,iits,tab);
  }

  static String iitk2iits(String text) {
    return convert(text,iitk,iits);
  }

  static String iitk2iscii(String text) {
    return convert(text,iitk,iscii);
  }

  static String iitk2tab(String text) {
    return convert(text,iitk,tab);
  }

  static String iscii2iits(String text) {
    return convert(text,iscii,iits);
  }

  static String iscii2iitk(String text) {
    return convert(text,iscii,iitk);
  }

  static String iscii2tab(String text) {
    return convert(text,iscii,tab);
  }

  static String tab2iits(String text) {
    return convert(text,tab,iits);
  }

  static String tab2iitk(String text) {
    return convert(text,tab,iitk);
  }

  static String tab2iscii(String text) {
    return convert(text,tab,iscii);
  }

  static String convert(String in,String x[],String y[])
  {
  Hashtable h = new Hashtable();
  for(int i=0;i<x.length;i++)
  h.put(x[i],y[i]);
  String out = "";
  String i = "";
  int l=0;
  int k=0;
  while (in.length() > 0) {
    if (in.length() < 5) { l = in.length(); } else { l = 5 ;}
    for(k = l; k > 0 ; k--) {
      i = in.substring(0,k);
      
      if (h.containsKey(i)) { 
      //System.out.println(i + "  " + h.get(i));
      out = out + (String)h.get(i);in = in.substring(k);break; }
    }
    if (k==0) { out = out + in.substring(0,1); in = in.substring(1);}
  }
  return out;
  }

}
