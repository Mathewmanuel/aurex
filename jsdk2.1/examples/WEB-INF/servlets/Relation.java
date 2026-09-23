import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.String.*;
import java.util.*;
//import Convert;

public class Relation extends HttpServlet{
 		
	PrintWriter out;
	Convert conv = new Convert();
  	     
	StringBuffer sb = new StringBuffer();
	String techTerm="";
	String srhWord = "";
	int disp;
	Hashtable hh;
	
	public void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException  {
		res.setContentType("text/html");
   		out=res.getWriter();
   		ArrayList list = new ArrayList();
		String fullIndex =req.getParameter("fullindex");
		System.out.println("full index= "+fullIndex);
		String posCatagory =req.getParameter("poss");
		String relNo =req.getParameter("techterm");
		String sense = req.getParameter("sense");
		String sense1 = sense.trim();
		StringTokenizer resulttokenizer=new StringTokenizer(fullIndex,"+");
		list.clear();
		while(resulttokenizer.hasMoreTokens()) {
			String key = resulttokenizer.nextToken();
			list.add(key);
		}
		try {
			int senseLen = sense1.length();
			if(senseLen == 0){
				techTerm = "";
				sb.setLength(0);
				Object indexArray[] = list.toArray();
				for(int i= 0;i<indexArray.length;i++){
					int sen = i + 1;
					String indexValue = indexArray[i].toString();
					Fetch fh = new Fetch();
					srhWord = fh.wordFetch(indexValue);
					final StringTokenizer tkn = new StringTokenizer(indexValue,":");
					out.print("<html>");
		    		out.print("<body bgcolor=\"#ffffff\" link=\"#0000ff\" vlink=\"#ff0000\" alink=\"#000088\">");
					out.print("<pre>");
					int f = 0;
					disp = 1;
					int relationNum = Integer.parseInt(relNo);
					hh = new Hashtable();
					while(tkn.hasMoreElements()){
						f++;
						String index = tkn.nextToken();
						relationFetch(index,posCatagory,relNo,sen,f);
						out.print("</pre></body></html>");
						sb.append("\n").append(techTerm);
					}
					if(hh.size() >0 && relationNum != 0){
						out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
						out.println(conv.iits2tab("poruL  "+sen));   // kum
						out.print("</font>");
                        out.print("<br>");
						resultDisplay(hh);	
					}
				}
			} else {
				techTerm="";
				sb.setLength(0);
				int sen = Integer.parseInt(sense1);
		   		String temp=list.get(sen-1).toString();
				String indexValue = temp;
				Fetch fh = new Fetch();
				srhWord = fh.wordFetch(indexValue);
				StringTokenizer passtokenizer=new StringTokenizer(indexValue,",");
		    	int length=passtokenizer.countTokens();
	       		out.print("<html>");
		    	out.print("<body bgcolor=\"#ffffff\" link=\"#0000ff\" vlink=\"#ff0000\" alink=\"#000088\">");
				out.print("<pre>");
				final StringTokenizer tkn = new StringTokenizer(indexValue,":");
				int f = 0;
				sb.setLength(0);						
				techTerm="";
				String s=null;
				disp = 1;
				int relationNum = Integer.parseInt(relNo);
				hh = new Hashtable();
				while(tkn.hasMoreElements()){
					f++;
					String index = tkn.nextToken();
					relationFetch(index,posCatagory,relNo,sen,f);
				}
				out.print("</pre></body></html>");
				if(hh.size() >0){
					out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
					out.println(conv.iits2tab("poruL  "+sen));   // kum
					out.print("</font>");
                    out.print("<br>");
					resultDisplay(hh);	
				}
				else {
					if(relationNum != 0){
						String s1 = "toTarpu illai";	
						out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");				
						out.println(conv.iits2tab(s1));
						out.print("</font>");	
					}	
				}		
			}
		//} catch(Exception exp) { out.println("Invalid DataType \n"); }
		} catch(Exception exp) { 
			out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
			out.println(conv.iits2tab("uLLiiTTai cari paarkkavum")); }
			out.print("</font>");	
	}
	
	public void resultDisplay(Hashtable h)
	{
		Enumeration keys = h.keys();
		ArrayList wordList = new ArrayList();
		ArrayList freqList = new ArrayList();
		while ( keys.hasMoreElements() )
   		{
	  		String key = (String )keys.nextElement();
	  		wordList.add(key);
	  		Frequency f = new Frequency();
	  		Integer freq = new Integer(f.findFreq(key));
	  		freqList.add(freq);
   		}
   		String word[] = new String[wordList.size()]; 
		word = (String[]) wordList.toArray(word);
		Integer wfreq[] = new Integer[freqList.size()];
		wfreq = (Integer[]) freqList.toArray(wfreq);
			 
		 for(int i=0;i<wfreq.length;i++){
			for(int j=i+1;j<wfreq.length;j++){
					
				if(wfreq[i].intValue() < wfreq[j].intValue()){
					Integer t = wfreq[i];
					wfreq[i] = wfreq[j];
					wfreq[j]=t;
					String temp = word[i];
					word[i]=word[j];
					word[j]=temp;
				}
			}
		}
		for(int i=0;i<word.length;i++){
			out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
			out.print(conv.iits2tab("=>"+word[i]));
			out.print("</font>");
			out.print("<br>");		
		}
	}

	public void relationFetch(String indexValue,String posCatagory,String relNo,int senNo,int tf) {
		String Dummy;
		StringBuffer getTechTerm = new StringBuffer();
		ResultSet rs,rs1,rs2;
		String query="";
		String query1 ="";
		String rel="";
		String q1="";
		String index="";
		int flag=0;
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
        	Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wordnet?user=arulmozi&password=onemoretry");
			int normal = indexValue.length();
			int passind=indexValue.lastIndexOf(",");
	        String jump = indexValue.substring(passind,normal);
        	int tempjump = jump.length();
	  		String passsu=indexValue.substring(0,passind+tempjump);
			StringTokenizer indtoken = new StringTokenizer(indexValue,",");
			int nindexx = passsu.length();
			String passsubmin = passsu.substring(0,nindexx-tempjump);
			int len = passsubmin.length();
			int n = (len - 1) / 2;
			String space =" ";
			StringTokenizer tok1 = new StringTokenizer(passsu,",");
			int indlen1 = tok1.countTokens();
			int sk=0;
			if (posCatagory.equals("Noun")) {
				if (relNo.equals("3")) {
					query = "(select label,nodeindex from twn where nodeindex  like '"+passsu+",%' and relation ='3' and label not like binary '"+srhWord+"')";
				} else if (relNo.equals("0")) {
         			if(tf == 1){
						out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
						out.print(conv.iits2tab("poruL  "+senNo));   
						out.print("</font>");
						out.print("<br>");
					}
					int firstword = 1;
					for(int i=0;i<n;i++) {
						query = "(select label,nodeindex from twn where nodeindex like '"+passsubmin+"' and label not like binary '"+srhWord+"')";
				   		Statement s = conn.createStatement();
						rs=s.executeQuery(query);
						while (rs.next()) {
							Dummy = rs.getString(1);
							Dummy = Dummy.replace('_',' ');
							flag = 1;
							space = space + "  ";
							techTerm = getTechTerm.append(space).append("==>").append(Dummy).append("\n").toString();
							out.print("<pre>");
							out.print(space+"==>");
							out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
							if(firstword == 1){
								out.print("<font color = red>");
								Dummy = Dummy.replace('_',' ');
								out.print(conv.iits2tab(Dummy));
								out.print("</font>");
								firstword++;
							}
							else {
								out.print(conv.iits2tab(Dummy));
							}
						}
						rs.close();
						passind = passsubmin.lastIndexOf(",");
						normal = passsubmin.length();
						jump = passsubmin.substring(passind,normal);
						tempjump = jump.length();
						passsu = passsubmin.substring(0,passind+tempjump);
						nindexx = passsu.length();
						passsubmin = passsu.substring(0,nindexx-tempjump);
					}	
					out.print("</font>");
					out.print("</pre>");
				} 
				else if(relNo.equals("4")){
					q1 = "(select relation from twn where nodeindex like '"+indexValue+"')";		
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()) {
						rel = rs.getString(1);
					}
					sk = Integer.parseInt(rel);
					if(sk != 4) {
						query1 = "(select label,nodeindex from twn where nodeindex like '"+passsu+",%' and relation ='4' and label not like binary '"+srhWord+"')";
					}
					else {
						query1 ="(select label,nodeindex from twn where nodeindex like '"+passsubmin+"') union (select label,nodeindex from twn where nodeindex like '"+passsubmin+",%' and nodeindex !='"+indexValue+"' and relation ='4' and label not like binary '"+srhWord+"')"; 
					}
				}
				else if (relNo.equals("1")){     
					query = "(select  label,nodeindex from twn where nodeindex  like '"+passsu+",%' and relation ='1' and label not like binary '"+srhWord+"')";
				}
				else if(relNo.equals("2")){
					q1 = "(select relation from twn where nodeindex like '"+indexValue+"')";
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()) {
						rel = rs.getString(1);
					}
					int k = Integer.parseInt(rel);
					if( k == 1) {
						query ="(select label,nodeindex from twn where nodeindex like '"+passsubmin+"')";		
					}
				}
				else if(relNo.equals("12")){
					q1 = "(select relation from twn where nodeindex like '"+indexValue+"')";
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()) {
						rel = rs.getString(1);
					}
					int k = Integer.parseInt(rel);
					if( k == 7) {
						query ="(select label,nodeindex from twn where nodeindex like '"+passsubmin+"')";		
					}	
				} 
				else if (relNo.equals("5")) {     
					query = "(select  label,nodeindex from twn where nodeindex  like '"+passsu+",%' and relation ='5' and label not like binary '"+srhWord+"')";
				} else { } 
			}
			else if (posCatagory.equals("Verb")) {
				if (relNo.equals("7")){     
					query = "(select  label,nodeindex from twn where nodeindex  like '"+passsu+",%' and relation ='7' and label not like binary '"+srhWord+"')";
				} else if(relNo.equals("4")){
					q1 = "(select relation from twn where nodeindex like '"+indexValue+"')";
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()) {
						rel = rs.getString(1);
					}
					sk = Integer.parseInt(rel);
					if(sk != 4) {
						query1 = "(select label,nodeindex from twn where nodeindex like '"+passsu+",%' and relation ='4' and label not like binary '"+srhWord+"')";
					}
					else {
						query1 ="(select label,nodeindex from twn where nodeindex like '"+passsubmin+"') union (select label,nodeindex from twn where nodeindex like '"+passsubmin+",%' and nodeindex !='"+indexValue+"' and relation ='4' and label not like binary '"+srhWord+"')";
					}
				}
				else if (relNo.equals("0")) {     
					if(tf == 1){
						out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
						out.print(conv.iits2tab("poruL  "+senNo));
						out.print("<br>");
						out.print("</font>");
					}
					int firstword = 1;
					for(int i=0;i<n;i++) {
						query = "(select label,nodeindex from twn where nodeindex like '"+passsubmin+"' and label not like binary '"+srhWord+"')";
         				Statement s = conn.createStatement();
						rs=s.executeQuery(query);
						while (rs.next()) {
							Dummy = rs.getString(1);
							Dummy = Dummy.replace('_',' ');
							flag = 1;
							space = space + "   ";
							techTerm = getTechTerm.append(space).append("==>").append(Dummy).append("\n").toString();
							out.print("<pre>");
							out.print(space+"==>");  
							out.print("<font face=TAB-Anna,tabanna,TAB_InaiMathi,TAB-Anna,tabakaram,tabmaduram,tabMaduram>");
							if(firstword == 1){
								Dummy = Dummy.replace('_',' ');
								out.print("<font color = red>");
								out.print(conv.iits2tab(Dummy));
								out.print("</font>");
								firstword++;
							}
							else{
								out.print(conv.iits2tab(Dummy));
							}
						}
						rs.close();
						passind = passsubmin.lastIndexOf(",");
						normal = passsubmin.length();
						jump = passsubmin.substring(passind,normal);
						tempjump = jump.length();
						passsu = passsubmin.substring(0,passind+tempjump);
						nindexx = passsu.length();
						passsubmin = passsu.substring(0,nindexx-tempjump);
					}
					out.print("</font>");
					out.print("</pre>");
				} else if (relNo.equals("10")) {
					query = "(select  label,nodeindex from twn where nodeindex  like '"+passsu+",%' and relation ='10' and label not like binary '"+srhWord+"')";
				}else if (relNo.equals("3")){     
					query = "(select  label,nodeindex from twn where nodeindex  like '"+passsu+",%' and relation ='3' and label not like binary '"+srhWord+"')";
	 			}else { }	
			}else if (posCatagory.equals("Adjective")) {
				if(relNo.equals("10")){
						query ="(select label,nodeindex from twn where nodeindex like '"+passsubmin+"')";	
				}  
				else if(relNo.equals("4")){
					q1 = "(select relation from twn where nodeindex like '"+indexValue+"')";
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()) {
						rel = rs.getString(1);
					}
					sk = Integer.parseInt(rel);
					if(sk != 4) {
						query1 = "(select label,nodeindex from twn where nodeindex like '"+passsu+",%' and relation ='4' and label not like binary '"+srhWord+"')";
					}
					else {
						query1 ="(select label,nodeindex from twn where nodeindex like '"+passsubmin+"') union (select label,nodeindex from twn where nodeindex like '"+passsubmin+",%' and nodeindex !='"+indexValue+"' and relation ='4' and label not like binary '"+srhWord+"')";
					}
				}
				
				else {  }
			}else if (posCatagory.equals("Adverb")) {
				if(relNo.equals("10")){
					query ="(select label,nodeindex from twn where nodeindex like '"+passsubmin+"')";	
				}   
	        	else if (relNo.equals("4")) {
		        	q1 = "(select relation from twn where nodeindex like '"+indexValue+"')";
					Statement s = conn.createStatement();
					rs=s.executeQuery(q1);
					while(rs.next()) {
						rel = rs.getString(1);
					}
					sk = Integer.parseInt(rel);
					if(sk != 4) {
						query1 = "(select label,nodeindex from twn where nodeindex like '"+passsu+",%' and relation ='4' and label not like binary '"+srhWord+"')";
					}
					else {
						query1 ="(select label,nodeindex from twn where nodeindex like '"+passsubmin+"') union (select label,nodeindex from twn where nodeindex like '"+passsubmin+",%' and nodeindex !='"+indexValue+"' and relation ='4' and label not like binary '"+srhWord+"')";
						
					}
				}
			}
			try {
				if ((relNo.equals("1")) || (relNo.equals("3")) || (relNo.equals("5")) || (relNo.equals("7")) || (relNo.equals("10")) || (relNo.equals("2")) || (relNo.equals("12")))
				{
					Statement s = conn.createStatement();
					rs=s.executeQuery(query);
					getTechTerm.setLength(0);
					Hashtable h = new Hashtable();
					while (rs.next()) {
						Dummy = rs.getString(1);
						Dummy = Dummy.replace('_',' ');
						String ind = rs.getString(2);
						StringTokenizer t = new StringTokenizer(ind,",");
						int indlen2 = t.countTokens();
						if((indlen1+1 == indlen2) || (indlen1 - 1 == indlen2)){
							flag = 1;
							h.put(Dummy,"");
							hh.put(Dummy,"");
						}
					}
					rs.close();
				}	
				else if ((relNo.equals("4"))) {
					Statement s = conn.createStatement();
					rs2=s.executeQuery(query1);
					Hashtable h = new Hashtable();
					while (rs2.next()) {
						Dummy = rs2.getString(1);
						Dummy = Dummy.replace('_',' ');
						String ind = rs2.getString(2);
						StringTokenizer t = new StringTokenizer(ind,",");
						int indlen2 = t.countTokens();
						if(sk == 4){
							if((indlen1 == indlen2) || (indlen1 - 1 == indlen2)){
								flag = 1;
								h.put(Dummy,"");
								hh.put(Dummy,"");
							}
						}
						else {
							if((indlen1 - 1 == indlen2) || (indlen1 + 1 == indlen2)){
								flag = 1;
								h.put(Dummy,"");
								hh.put(Dummy,"");
							}
						}
						techTerm = getTechTerm.append("->").append(Dummy).append("\n").toString();
					}
					rs2.close();
				}
			} catch(Exception e) { }
			conn.close();
		} catch(Exception e) { }
	}
	
	public class Frequency {
		ResultSet rs;
		public int findFreq(String str){
			String qry = "select freq from frequency where word like binary '"+str+"'";
			int freq=0;
			try {
				Class.forName("org.gjt.mm.mysql.Driver");
				Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wordnet?user=arulmozi&password=onemoretry");
				Statement s = conn.createStatement();
				rs=s.executeQuery(qry);
				while(rs.next()){
					freq = Integer.parseInt(rs.getString(1));
				}
			}
			catch(Exception ei) { }
			return freq;
		}
	}
	
	public class Fetch {
		public String wordFetch(String indexValue){
			String qry = "select label from sense where hypernym='"+indexValue+"'";
			String word="";
			ResultSet rs;
			try {
				Class.forName("org.gjt.mm.mysql.Driver");
				Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wordnet?user=arulmozi&password=onemoretry");
				Statement s = conn.createStatement();
				rs=s.executeQuery(qry);
				while(rs.next()){
					word = rs.getString(1);;
				}
			}
			catch(Exception ei) { }
			return word;	
		}
	}
}
