package com.tencent.mapsdkdemo.raster;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.RouteOverlay;
import com.tencent.tencentmap.mapsdk.route.QPlaceInfo;
import com.tencent.tencentmap.mapsdk.route.QRouteSearchResult;
import com.tencent.tencentmap.mapsdk.route.RouteSearch;

/**
 * 地图显示，默认情况下需要继承自mapactivity,继承了mapactivity后，soso mapsdk负责管理地图的生存周期，
 * 如果不继承mapactivity,比如继承自activity,fragment等，需要在父组件的生命周期调用地图的相关接口，比如继承自
 * activity的，则要在activity的ondestroy方法，调用mapview.ondestroy()等。
 * 
 *
 */
public class RouteSearchDemo extends MapActivity {

	MapView mMapView;
	RouteOverlay busRouteOverlay=null;
	RouteOverlay driveRouteOverlay=null;
	Button btnRouteSearch = null;

	@Override
	/**
	 *显示地图，启用内置缩放控件，并用MapController控制地图的中心点及Zoom级别
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routesearchdemo);
		mMapView = (MapView) findViewById(R.id.mapviewroutesearch);
		
		btnRouteSearch = (Button) this.findViewById(R.id.btnRouteSearch);
		btnRouteSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchBusRoute();
//				searchDriveRoute();
				
			}
		});
		
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		GeoPoint point = new GeoPoint((int) (39.90923 * 1E6), (int) (116.397428 * 1E6)); // 用给定的经纬度构造一个GeoPoint，单位是微度
										
		mMapView.getController().setCenter(point); 
		mMapView.getController().setZoom(9);
		mMapView.setBuiltInZoomControls(false);
	}
	
	private void searchBusRoute()
	{
		RouteSearch routeSearch=new RouteSearch(RouteSearchDemo.this);
		QPlaceInfo placeStart=new QPlaceInfo();
		placeStart.point=new GeoPoint((int)(39.981857*1e6),(int)(116.306364*1e6));
		QPlaceInfo placeEnd=new QPlaceInfo();
		placeEnd.point=new GeoPoint((int)(39.900732*1e6),(int)(116.433547*1e6));
		
		QRouteSearchResult busRouteResult=null;
		try {
			busRouteResult=routeSearch.searchBusRoute("北京", placeStart, placeEnd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(busRouteResult==null)
		{
			return;
		}
		
		if(busRouteOverlay==null)
		{
			busRouteOverlay=new RouteOverlay();
			mMapView.addOverlay(busRouteOverlay);
		}
		busRouteOverlay.setBusRouteInfo(busRouteResult.busRoutePlanInfo.routeList.get(0));
		
		busRouteOverlay.showInfoWindow(0);
		
		zoomToSpan(busRouteResult.busRoutePlanInfo.routeList.get(0).routeNodeList);
	}
	
	private void searchDriveRoute()
	{
		RouteSearch routeSearch=new RouteSearch(RouteSearchDemo.this);
		QPlaceInfo placeStart=new QPlaceInfo();
		placeStart.point=new GeoPoint((int)(39.981857*1e6),(int)(116.306364*1e6));
		QPlaceInfo placeEnd=new QPlaceInfo();
		placeEnd.point=new GeoPoint((int)(39.900732*1e6),(int)(116.433547*1e6));
		
		QRouteSearchResult driveSearchResult=null;
		try {
			driveSearchResult=routeSearch.searchDriveRoute("北京", placeStart, "北京", placeEnd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		if(driveSearchResult==null)
		{
			return;
		}
		if(driveRouteOverlay==null)
		{
			driveRouteOverlay=new RouteOverlay();
			mMapView.addOverlay(driveRouteOverlay);
		}
		driveRouteOverlay.setDriveRouteInfo(driveSearchResult.driveRouteInfo);
		driveRouteOverlay.showInfoWindow(0);
	}
	
	/**
	 * 把地图视图缩放到刚好显示路线   
	 * @param listPts
	 */
	private void zoomToSpan(List<GeoPoint> listPts)
	{
		if(listPts==null)
		{
			return;
		}
		int iPtSize=listPts.size();
		if(iPtSize<=0)
		{
			return;
		}
		
		GeoPoint geoPtLeftUp=null;
		GeoPoint geoPtRightDown=null;  //获取路径点的左上角点，和右下角点   
		
		GeoPoint geoPt=null;
		for(int i=0;i<iPtSize;i++)
		{
			geoPt=listPts.get(i);
			if(geoPt==null)
			{
				continue;
			}
			
			if(geoPtLeftUp==null)
			{
				geoPtLeftUp=new GeoPoint(geoPt.getLatitudeE6(),geoPt.getLongitudeE6());
			}
			else
			{
				if(geoPtLeftUp.getLatitudeE6()<geoPt.getLatitudeE6())
				{
					geoPtLeftUp.setLatitudeE6(geoPt.getLatitudeE6());
				}
				if(geoPtLeftUp.getLongitudeE6()>geoPt.getLongitudeE6())
				{
					geoPtLeftUp.setLongitudeE6(geoPt.getLongitudeE6());
				}
			}
			
			if(geoPtRightDown==null)
			{
				geoPtRightDown=new GeoPoint(geoPt.getLatitudeE6(),geoPt.getLongitudeE6());
			}
			else
			{
				if(geoPtRightDown.getLatitudeE6()>geoPt.getLatitudeE6())
				{
					geoPtRightDown.setLatitudeE6(geoPt.getLatitudeE6());
				}
				if(geoPtRightDown.getLongitudeE6()<geoPt.getLongitudeE6())
				{
					geoPtRightDown.setLongitudeE6(geoPt.getLongitudeE6());
				}
			}
			
		}
		
		if(geoPtLeftUp==null||geoPtRightDown==null)
		{
			return;
		}
		mMapView.getController().zoomToSpan(geoPtLeftUp, geoPtRightDown);
	}

	
}
