package com.tencent.mapsdkdemo.raster;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.PoiOverlay;
import com.tencent.tencentmap.mapsdk.search.PoiItem;
import com.tencent.tencentmap.mapsdk.search.PoiResults;
import com.tencent.tencentmap.mapsdk.search.PoiSearch;

/**
 * 地图显示，默认情况下需要继承自mapactivity,继承了mapactivity后，soso mapsdk负责管理地图的生存周期，
 * 如果不继承mapactivity,比如继承自activity,fragment等，需要在父组件的生命周期调用地图的相关接口，比如继承自
 * activity的，则要在activity的ondestroy方法，调用mapview.ondestroy()等。
 * 
 *
 */
public class PoiSearchDemo extends MapActivity {

	MapView mMapView;
	PoiSearch poiSearch=null;
	PoiOverlay myPoiOverlay=null;

	EditText editTextKeyworkd=null;
	Button btnPoiSearch = null;

	@Override
	/**
	 *显示地图，启用内置缩放控件，并用MapController控制地图的中心点及Zoom级别
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poisearchdemo);
		mMapView = (MapView) findViewById(R.id.mapviewpoisearch);
		
		editTextKeyworkd=(EditText)findViewById(R.id.txtKeyword);
		
		btnPoiSearch = (Button) this.findViewById(R.id.btnPoiSearch);
		btnPoiSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String strKeyWord=editTextKeyworkd.getText().toString();
				if(strKeyWord==null||strKeyWord.trim().equals(""))
				{
					showTipDialog();
					return;
				}
				if(poiSearch==null)
				{
					poiSearch=new PoiSearch(PoiSearchDemo.this);
				}
				
				PoiResults poiResult=null;
//				GeoPoint geoCenter=new GeoPoint((int)(39.917237*1e6),(int)(116.39757*1e6));
				/*GeoPoint geoLeftButtom=new GeoPoint((int)(39.896775*1e6),(int)(116.354213*1e6));
				GeoPoint geoRightTop=new GeoPoint((int)(39.936273*1e6),(int)(116.440043*1e6));*/
				try {
//					poiResult=poiSearch.searchPoiInBound(strKeyWord, geoLeftButtom, geoRightTop);
//					poiResult=poiSearch.searchPoiInCircle("故宫", geoCenter, 1000);
					poiResult=poiSearch.searchPoiInCity(strKeyWord, "广州");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				
				List<PoiItem> listPois=poiResult.getCurrentPagePoiItems();
				if(listPois==null)
				{
					return;
				}
				
				if(myPoiOverlay==null)
				{
					myPoiOverlay = new PoiOverlay(null);
					mMapView.addOverlay(myPoiOverlay);
				}
				myPoiOverlay.setPoiItems(listPois);
				myPoiOverlay.showInfoWindow(0);
			}
		});
		
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		GeoPoint point = new GeoPoint((int) (39.90923 * 1E6), (int) (116.397428 * 1E6)); // 用给定的经纬度构造一个GeoPoint，单位是微度
																		// (度
		
		mMapView.getController().setCenter(point); 
		mMapView.getController().setZoom(9);
	}
	
	private void showTipDialog()
	{
		AlertDialog dlg=null;
		AlertDialog.Builder builder = new Builder(PoiSearchDemo.this);
		builder.setMessage("请输入查询关键字");
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
