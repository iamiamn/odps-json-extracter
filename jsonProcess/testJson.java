package jsonProcess;

public class testJson {
	public static void main(String[] args){
		String test1 = " [{\"time\":7149,\"type\":0,\"target\":\"****\"},{\"time\":5718,\"type\":1,\"target\":\"****\"}" + 
    ",{\"time\":7114,\"type\":0,\"target\":\"****\"},{\"time\":7121,\"type\":1,\"target\":\"****\"}," +
    "{\"time\":7153,\"type\":1,\"target\":\"****\"},{\"time\":13593,\"type\":0,\"target\":\"****\"] ";//最后的对象缺少了}，因此会导致unterminated object
    //这个exception会出现在大量处理json字符串的情况，因此我在evalute函数中使用了try catch 捕捉并返回全部-1的结果
    //string带双引号时，用转义字符\"
		String test2 = "[]";//测试空字符串
    String test3 = "[{\"time\":205,\"type\":1,\"target\":\"****\"},{\"time\":17046,\"type\":0,\"target\":\"****\"}]";
		parseJson1 gj1 = new parseJson1();
		String result = gj1.evaluate(test1,"1");
		System.out.println(result);
		result = gj1.evaluate(test2,"3");
		System.out.println(result);
		result = gj1.evaluate(test3,"1");
		System.out.println(result);
		String kb = "[{\"time\":222403,\"key\":\"*\",\"target\":\"****\"},{\"time\":221899,\"key\":\"*\",\"target\":\"****\"},{\"time\":215698,\"key\":\"*\",\"target\":\"****\"},{\"time\":213673,\"key\":\"*\",\"target\":\"****\"},{\"time\":211841,\"key\":\"*\",\"target\":\"****\"},{\"time\":209345,\"key\":\"*\",\"target\":\"****\"},{\"time\":205945,\"key\":\"*\",\"target\":\"****\"},{\"time\":205288,\"key\":\"*\",\"target\":\"****\"},{\"time\":213249,\"key\":\"*\",\"target\":\"****\"},{\"time\":214913,\"key\":\"*\",\"target\":\"****\"},{\"time\":216770,\"key\":\"*\",\"target\":\"****\"},{\"time\":217995,\"key\":\"*\",\"target\":\"****\"}]";
		result = gj1.evaluate(kb,"7");
		System.out.println(result);
		String MC = "[{\"button\":\"left\",\"ew\":134,\"ex\":911,\"eh\":118,\"ey\":523,\"x\":964,\"y\":525,\"time\":2672,\"attr\":\"\",\"target\":\"****\"},{\"button\":\"left\",\"x\":1046,\"y\":369,\"time\":3300,\"attr\":\"\",\"target\":\"\"},{\"button\":\"left\",\"x\":1047,\"y\":450,\"time\":3895,\"attr\":\"\",\"target\":\"****\"},{\"button\":\"left\",\"x\":982,\"y\":458,\"time\":5003,\"attr\":\"\",\"target\":\"****\"}]";
		result = gj1.evaluate(MC,"3");
		System.out.println(result);

	}
}
