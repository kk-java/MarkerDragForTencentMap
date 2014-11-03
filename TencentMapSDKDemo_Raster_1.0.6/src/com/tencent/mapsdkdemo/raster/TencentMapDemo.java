package com.tencent.mapsdkdemo.raster;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapController;
import com.tencent.tencentmap.mapsdk.map.MapView;

/**
 * 地图显示，默认情况下需要继承自mapactivity,继承了mapactivity后，soso mapsdk负责管理地图的生存周期，
 * 如果不继承mapactivity,比如继承自activity,fragment等，需要在父组件的生命周期调用地图的相关接口，比如继承自
 * activity的，则要在activity的ondestroy方法，调用mapview.ondestroy()等。
 * 
 *
 */
public class TencentMapDemo extends MapActivity {

	MapView mMapView;
	MapController mMapController;

	Button btnTraffic = null;
	Button btnAnimationTo=null;
	Button btnZoomSatellite=null;

	@Override
	/**
	 *显示地图，启用内置缩放控件，并用MapController控制地图的中心点及Zoom级别
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapviewdemo);
		mMapView = (MapView) findViewById(R.id.mapviewtraffic);
		
		btnTraffic = (Button) this.findViewById(R.id.btnTraffic);
		btnTraffic.setText("打开实时交通");
		btnTraffic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean boTraffic = mMapView.isTraffic();
				if (boTraffic == false) {
					int iCurrentLevel=mMapView.getZoomLevel();
					if(iCurrentLevel<10)  //实时交通在10级以上才显示     
					{
						mMapView.getController().setZoom(10);
					}
					mMapView.setTraffic(true);
					btnTraffic.setText("关闭实时交通");
				} else {
					mMapView.setTraffic(false);
					btnTraffic.setText("打开实时交通");
				}
			}
		});

		btnAnimationTo=(Button)this.findViewById(R.id.btnAnimationTo);
		btnAnimationTo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GeoPoint ge=new GeoPoint((int) (39.95923 * 1E6), (int) (116.437428 * 1E6)); 
				Runnable runAnimate=new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(TencentMapDemo.this, "animation finish", Toast.LENGTH_LONG).show();
					}};
				mMapView.getController().animateTo(ge,runAnimate);
			}});
		
		btnZoomSatellite=(Button)this.findViewById(R.id.btnSatellite);
		btnZoomSatellite.setText("打开卫星影像");
		btnZoomSatellite.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean boSatellite=mMapView.isSatellite();
				if(boSatellite==true)
				{
					mMapView.setSatellite(false);
					btnZoomSatellite.setText("打开卫星影像");
				}
				else
				{
					mMapView.setSatellite(true);
					btnZoomSatellite.setText("关闭卫星影像");
				}
				
			}});
		
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		mMapController = mMapView.getController(); // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		
		mMapController.setZoom(9);
	}
	
}
