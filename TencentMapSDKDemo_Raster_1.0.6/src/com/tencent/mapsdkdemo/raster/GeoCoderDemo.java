package com.tencent.mapsdkdemo.raster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.search.GeocoderResult;
import com.tencent.tencentmap.mapsdk.search.GeocoderSearch;
import com.tencent.tencentmap.mapsdk.search.PoiItem;
import com.tencent.tencentmap.mapsdk.search.ReGeocoderResult;
import com.tencent.tencentmap.mapsdk.search.ReGeocoderResult.ReGeocoderAddress;

/**
 * 地图显示，默认情况下需要继承自mapactivity,继承了mapactivity后，腾讯 mapsdk负责管理地图的生存周期，
 * 如果不继承mapactivity,比如继承自activity,fragment等，需要在父组件的生命周期调用地图的相关接口，比如继承自
 * activity的，则要在activity的ondestroy方法，调用mapview.ondestroy()等。
 * 
 *
 */
public class GeoCoderDemo extends Activity {

	GeocoderSearch geocodersearcher=null;

	EditText edittextGeoCoder=null;
	EditText edittextReGeoCoder=null;
	EditText edittextResult=null;
	
	Button btnGeocoder = null;
	Button btnRegeocoder=null;

	@Override
	/**
	 *显示地图，启用内置缩放控件，并用MapController控制地图的中心点及Zoom级别
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geocoderdemo);
		
		edittextGeoCoder=(EditText)findViewById(R.id.txtgeocoder);
		edittextReGeoCoder=(EditText)findViewById(R.id.txtregeocoder);
		edittextResult=(EditText)findViewById(R.id.txtgeocoderresult);
		
		btnGeocoder=(Button)findViewById(R.id.btnGeocoder);
		btnRegeocoder=(Button)findViewById(R.id.btnRegeocoder);
		
		geocodersearcher=new GeocoderSearch(GeoCoderDemo.this);
		
		btnGeocoder.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String strGeocoder=edittextGeoCoder.getText().toString();
				strGeocoder=strGeocoder.trim();
				if(strGeocoder=="")
				{
					showTipDialog("请输入查询关键字");
					return;
				}
				GeocoderResult geocoderResult=null;
				try {
					geocoderResult=geocodersearcher.searchFromLocationName(strGeocoder);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(geocoderResult==null)
				{
					edittextResult.setText("");
					return;
				}
				String nextLine = System.getProperty("line.separator"); 
				
				String strResult="";
				strResult+="province="+geocoderResult.province+nextLine;
				strResult+="city="+geocoderResult.city+nextLine;
				strResult+="district"+geocoderResult.district+nextLine;
				strResult+="name="+geocoderResult.name+nextLine;
				strResult+="point="+geocoderResult.point.toString();
				edittextResult.setText(strResult);
			}});
		
		btnRegeocoder.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String strReGeocoder=edittextReGeoCoder.getText().toString();
				strReGeocoder=strReGeocoder.trim();
				if(strReGeocoder=="")
				{
					showTipDialog("请输入坐标");
					return;
				}
				String[] strCoord=strReGeocoder.split(",");
				if(strCoord==null||strCoord.length!=2)
				{
					showTipDialog("输入坐标格式不正确");
					return;
				}
				double dLat=0;
				double dlng=0;
				try
				{
					dLat=Double.parseDouble(strCoord[0]);
					dlng=Double.parseDouble(strCoord[1]);
				}
				catch(Exception e)
				{
					showTipDialog("输入坐标格式不正确");
					return;
				}
				
				GeoPoint geoRegeocoder=new GeoPoint((int)(dLat*1e6),(int)(dlng*1e6));
				ReGeocoderResult regeocoderResult=null;
				try {
					regeocoderResult=geocodersearcher.searchFromLocation(geoRegeocoder);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(regeocoderResult==null)
				{
					edittextResult.setText("");
					return;
				}
				String nextLine = System.getProperty("line.separator"); 
				
				String strResult="";
				if(regeocoderResult.addresslist!=null)            
				{
					int iAddrSize=regeocoderResult.addresslist.size();
					strResult+="addresslist size="+iAddrSize+nextLine;
					if(iAddrSize>0)
					{
						ReGeocoderAddress addr=regeocoderResult.addresslist.get(0);
						strResult+="address name="+addr.name+nextLine;
						strResult+="address type="+addr.type+nextLine;
						strResult+="address dist="+addr.dist+nextLine;
						strResult+="address adcode="+addr.adcode+nextLine;
						strResult+="address point="+addr.point.toString()+nextLine;
					}
				}
				if(regeocoderResult.poilist!=null)            
				{
					int iPoiSize=regeocoderResult.poilist.size();
					strResult+="poilist size="+iPoiSize+nextLine;
					if(iPoiSize>0)
					{
						PoiItem poi=regeocoderResult.poilist.get(0);
						strResult+="poi name="+poi.name+nextLine;
						strResult+="poi address="+poi.address+nextLine;
						strResult+="poi classes="+poi.classes+nextLine;
						strResult+="poi phone="+poi.phone+nextLine;
						strResult+="poi point="+poi.point.toString()+nextLine;
					}
				}
				
				edittextResult.setText(strResult);
			}});
		
	}
	
	private void showTipDialog(String strTip)
	{
		AlertDialog dlg=null;
		AlertDialog.Builder builder = new Builder(GeoCoderDemo.this);
		builder.setMessage(strTip);
		builder.setTitle("提示");
		builder.setPositiveButton("确定",  new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			dialog.dismiss();
		}});
		dlg=builder.create();
		dlg.show();
	}
	
}
