package jsonProcess;
import com.aliyun.odps.udf.UDF;
import com.google.gson.Gson;
import java.util.Arrays;
import java.lang.Math;

public final class parseJson extends UDF {
	

	//新建getMIn和getMax，getVariance等统计函数
	public static double getMin(double[] inputData) {
		  if (inputData == null || inputData.length == 0)
		   return -1;
		  int len = inputData.length;
		  double min = inputData[0];
		  for (int i = 0; i < len; i++) {
		   if (min > inputData[i])
		    min = inputData[i];
		  }
		  return min;
		 }
	public static double getMax(double[] inputData) {
		  if (inputData == null || inputData.length == 0)
		   return -1;
		  int len = inputData.length;
		  double max = inputData[0];
		  for (int i = 0; i < len; i++) {
		   if (max < inputData[i])
		    max = inputData[i];
		  }
		  return max;
		 }
	public static double getSum(double[] inputData) {
		  if (inputData == null || inputData.length == 0)
		   return -1;
		  int len = inputData.length;
		  double sum = 0;
		  for (int i = 0; i < len; i++) {
		   sum = sum + inputData[i];
		  }

		  return sum;

		 }
	
	public static double getAverage(double[] inputData) {
		  if (inputData == null || inputData.length == 0)
		   return -1;
		  int len = inputData.length;
		  double result;
		  result = getSum(inputData) / len;
		  
		  return result;
		 }
	    
	public static double getVariance(double[] inputData) {
	    	  int count = getCount(inputData);
	    	  double sqrsum = getSquareSum(inputData);
	    	  double average = getAverage(inputData);
	    	  double result;
	    	  result = (sqrsum - count * average * average) / count;

	    	     return Math.sqrt(result);//这里求方差，其实被我改成了标准差
	    	 }
	    
	public static double getSquareSum(double[] inputData) {
	    	  if(inputData==null||inputData.length==0)
	    	      return -1;
	    	     int len=inputData.length;
	    	  double sqrsum = 0.0;
	    	  for (int i = 0; i <len; i++) {
	    	   sqrsum = sqrsum + inputData[i] * inputData[i];
	    	  }
	    	  return sqrsum;
	    	  
	    }
	    
	public static int getCount(double[] inputData) {
	    		  if (inputData == null)
	    		   return -1;

	    		  return inputData.length;
	    		 }
	    
	//为了提取json字符串为scala object， 我们需要根据json对象内容新建类，例如下面的json对象数组
	//[{\"time\":7149,\"type\":0,\"target\":\"****\"},{\"time\":5718,\"type\":1,\"target\":\"****\"}]
	//这个json字符串包含两个对象，没个对象都包含有time\type\target这三个属性，我们要提取的只有time和type，因此只需要新建一个包含这两个成员的类
	//类内还必须为每个类的成员编写getter和setter函数，使用eclipse 可以右键java包，自动化为类内的全部对象添加对应的getter和setter函数
	public static class ElemFocus{//新建类，	针对的json字符串:[{\"time\":7149,\"type\":0,\"target\":\"****\"}
		public int time;
		public int type;
		public String target;
		//
		public int getTime() {
			return time;
		}
		public void setTime(int time) {
			this.time = time;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public String getTarget() {
			return target;
		}
		public void setTarget(String target) {
			this.target = target;
		}
		
		
	}	
	public  String extractEF(String strJson){//传入json字符串，提取字符串内所有的elemfoucs类对象，返回统计信息
		Gson myGson = new Gson();
		double type1_rate, time_sd, time_range;
		
		double type1_count = 0;
        int total_count = 0;
        
		
		ElemFocus[] elems = myGson.fromJson(strJson,ElemFocus[].class);
		if (elems.length == 0) return "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1";
		double[] time_series = new double[elems.length];
		if (elems.length == 0){
			type1_rate = 0;
			time_sd = 0;
			time_range = 0;
		}
		else {
			for (ElemFocus elem: elems){
				time_series[total_count] = elem.time;
				total_count += 1;				
				if (elem.type == 1){
					type1_count += 1;
				}
			}
			type1_rate = type1_count / total_count;//如果两个都是int，计算结果为0
			time_sd = getVariance(time_series);
			time_range = getMax(time_series) - getMin(time_series);
		}
		
		return String.valueOf(total_count) + " " + String.valueOf(type1_rate) + " " +
		String.valueOf(time_sd) + " " + String.valueOf( time_range);	
		
	}
	public static class MouseClick{
		public String Button,attr,target;
		public int x, y,time;
		public String getButton() {
			return Button;
		}
		public void setButton(String button) {
			Button = button;
		}
		public String getAttr() {
			return attr;
		}
		public void setAttr(String attr) {
			this.attr = attr;
		}
		public String getTarget() {
			return target;
		}
		public void setTarget(String target) {
			this.target = target;
		}
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		public int getTime() {
			return time;
		}
		public void setTime(int time) {
			this.time = time;
		}
	}
	public String extractMC(String strJson){
		
		Gson myGson = new Gson();
		
		
		MouseClick[] elems = myGson.fromJson(strJson, MouseClick[].class);
		if (elems.length == 0) return "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1";
		int total_count = 0;
		double left_count = 0;
		double[] x_series = new double[elems.length];
		double[] y_series = new double[elems.length];
		double[] time_series = new double[elems.length];
		for (MouseClick elem : elems){
			x_series[total_count] = elem.x;
			y_series[total_count] = elem.y;
			time_series[total_count] = elem.time;
			total_count += 1;
			if (elem.Button == "left") left_count += 1;
		}
		double left_rate = left_count/total_count;
		Arrays.sort(time_series);
		double[] time_sep = new double[elems.length - 1];
		for (int i = 0; i < total_count - 1; i++){
			time_sep[i] = time_series[i + 1] - time_series[i]; 
		}
		double x_range = getMax(x_series) - getMin(x_series);
		double y_range = getMax(y_series) - getMin(y_series);
		double time_range = getMax(time_series) - getMin(time_series);
		return String.valueOf(total_count) + " " +String.valueOf(getAverage(time_sep)) + 
				" " + String.valueOf(time_range) + " " + String.valueOf(getVariance(x_series)) 
				+ " " + String.valueOf(getVariance(y_series))+ " " + String.valueOf(x_range)
		+ " " + String.valueOf(y_range) + " " + String.valueOf(left_rate);
		
		
	}
	public static class MouseMove{
		public int x, y, t;

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getT() {
			return t;
		}

		public void setT(int t) {
			this.t = t;
		}
	}
	public String extractMM(String strJson){
		Gson myGson = new Gson();
		
		
		MouseMove[] elems = myGson.fromJson(strJson, MouseMove[].class);
		if (elems.length == 0) return "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1";
		int total_count = 0;
		double[] x_series = new double[elems.length];
		double[] y_series = new double[elems.length];
		double[] time_series = new double[elems.length];
		for (MouseMove elem : elems){
//			ew_series[total_count] = elem.ew;
//			ex_series[total_count] = elem.ex;
			//未完
			x_series[total_count] = elem.x;
			y_series[total_count] = elem.y;
			time_series[total_count] = elem.t;
			total_count += 1;
		}
		Arrays.sort(time_series);
		double[] time_sep = new double[elems.length - 1];
		for (int i = 0; i < total_count - 1; i++){
			time_sep[i] = time_series[i + 1] - time_series[i]; 
		}
		double x_range = getMax(x_series) - getMin(x_series);
		double y_range = getMax(y_series) - getMin(y_series);
		double time_range = getMax(time_series) - getMin(time_series);//!!!考虑totalConunt也输出？
		return String.valueOf(getAverage(time_sep)) + " " + String.valueOf(time_range) 
		+ " " + String.valueOf(getVariance(x_series)) + " " + String.valueOf(getVariance(y_series))+ " " + String.valueOf(x_range)
		+ " " + String.valueOf(y_range) + " " + String.valueOf(total_count);
	}
	public static class MouseSample{
		public int x, y;

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
	}
	public String extractMS(String strJson){
		Gson myGson = new Gson();
		
		
		MouseSample[] elems = myGson.fromJson(strJson, MouseSample[].class);
		if (elems.length == 0) return "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1";
		int total_count = 0;
		double[] x_series = new double[elems.length];
		double[] y_series = new double[elems.length];
		for (MouseSample elem : elems){
			x_series[total_count] = elem.x;
			y_series[total_count] = elem.y;
			total_count += 1;
		}
		double x_range = getMax(x_series) - getMin(x_series);
		double y_range = getMax(y_series) - getMin(y_series);

		return String.valueOf(getVariance(x_series)) + " " + String.valueOf(getVariance(y_series))+ " " + String.valueOf(x_range)
		+ " " + String.valueOf(y_range);
	}
	public static class Keyboard{
		int time;
		public int getTime() {
			return time;
		}
		public void setTime(int time) {
			this.time = time;
		}
	}
	public String extractKB(String strJson){
		Gson myGson = new Gson();
		
		
		Keyboard[] elems = myGson.fromJson(strJson, Keyboard[].class);
		if (elems.length == 0) return "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1";
		int total_count = 0;
		double[] time_series = new double[elems.length];
		for (Keyboard elem : elems){
			time_series[total_count] = elem.time;
			total_count += 1;
		}
		double time_range = getMax(time_series) - getMin(time_series);
		Arrays.sort(time_series);
		String result = String.valueOf(total_count) + " " + String.valueOf(getVariance(time_series)) + " " + String.valueOf(time_range);
		return result;
	}
	public String evaluate(String s, String jsonType){
		s = s.trim();
		int typing = Integer.valueOf(jsonType);
		try{
			if (typing == 1){
				return extractEF(s);
			}
			else if (typing == 3){
				return extractMC(s);
			}
			else if (typing == 4){
				return extractMM(s);
			}
			else if (typing == 5){
				return extractMS(s);
			}
			else if (typing == 7){
				return extractKB(s);
			}
			else return "fail";//必须加上
			
		}catch(Exception e){
			System.out.println(s);
//			e.printStackTrace();
			return "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1";
		}
		
		
	}
	public String evaluate(String s){//不能使用static 关键字
		return "can";//默认不加任何尾部，直接空格
	}

}
