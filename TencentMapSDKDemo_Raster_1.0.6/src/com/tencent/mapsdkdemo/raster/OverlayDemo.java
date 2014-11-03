package com.tencent.mapsdkdemo.raster;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;

import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.Overlay;
import com.tencent.tencentmap.mapsdk.map.Projection;

/**
 * 地图显示，默认情况下需要继承自mapactivity,继承了mapactivity后，soso mapsdk负责管理地图的生存周期，
 * 如果不继承mapactivity,比如继承自activity,fragment等，需要在父组件的生命周期调用地图的相关接口，比如继承自
 * activity的，则要在activity的ondestroy方法，调用mapview.ondestroy()等。
 * 
 *
 */
public class OverlayDemo extends MapActivity {

	MapView mMapView;

	@Override
	/**
	 *显示地图，启用内置缩放控件，并用MapController控制地图的中心点及Zoom级别
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overlaydemo);
		mMapView = (MapView) findViewById(R.id.mapviewOverlay);
		
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件 
		mMapView.getController().setZoom(9);
		
		Bitmap bmpMarker=null;
		Resources res=OverlayDemo.this.getResources();
		bmpMarker=BitmapFactory.decodeResource(res, R.drawable.mark_location);
		
		SimulateLocationOverlay simuOvelay=new SimulateLocationOverlay(bmpMarker);
		mMapView.addOverlay(simuOvelay);
		
		GeoPoint geoSimulateLocation=new GeoPoint((int)(39.984297*1e6), (int)(116.307523*1e6));
		simuOvelay.setGeoCoords(geoSimulateLocation);
		simuOvelay.setAccuracy(5000);
		
		
		GraphicOverlay garphicOverlay=new GraphicOverlay();
		mMapView.addOverlay(garphicOverlay);
	}
	
}

class GraphicOverlay extends Overlay
{
	GeoPoint geoPolyLine1=new GeoPoint((int)(39.711597*1e6),(int)(116.209837*1e6));
	GeoPoint geoPolyLine2=new GeoPoint((int)(39.781512*1e6),(int)(116.381719*1e6));
	GeoPoint geoPolyLine3=new GeoPoint((int)(39.793441*1e6),(int)(116.722157*1e6));
	GeoPoint geoPolyLine4=new GeoPoint((int)(40.011219*1e6),(int)(116.562473*1e6));
	
	
	GeoPoint geoPolygon1=new GeoPoint((int)(39.949054*1e6),(int)(116.443058*1e6));
	GeoPoint geoPolygon2=new GeoPoint((int)(39.907316*1e6),(int)(116.624484*1e6));
	GeoPoint geoPolygon3=new GeoPoint((int)(39.821312*1e6),(int)(116.578039*1e6));
	GeoPoint geoPolygon4=new GeoPoint((int)(39.857197*1e6),(int)(116.392259*1e6));
	
	@Override
	public void draw(Canvas canvas, MapView mapView) 
	{
		drawPolyline(canvas,mapView);
		drawPolygon(canvas,mapView);
	}
	
	private void drawPolyline(Canvas canvas, MapView mapView)
	{
		Point ptLine1=mapView.getProjection().toPixels(geoPolyLine1, null);
		Point ptLine2=mapView.getProjection().toPixels(geoPolyLine2, null);
		Point ptLine3=mapView.getProjection().toPixels(geoPolyLine3, null);
		Point ptLine4=mapView.getProjection().toPixels(geoPolyLine4, null);
		
		Path pathLine=new Path();
		pathLine.moveTo(ptLine1.x, ptLine1.y);
		pathLine.lineTo(ptLine2.x, ptLine2.y);
		pathLine.lineTo(ptLine3.x, ptLine3.y);
		pathLine.lineTo(ptLine4.x, ptLine4.y);
		
		Paint paintLine=new Paint();
		paintLine.setStyle(Style.STROKE);
		paintLine.setStrokeWidth(10);
		paintLine.setColor(Color.BLUE);
		paintLine.setAntiAlias(true);
		paintLine.setStrokeCap(Cap.ROUND);
		paintLine.setStrokeJoin(Join.ROUND);
		
		canvas.drawPath(pathLine, paintLine);
	}
	
	private void drawPolygon(Canvas canvas, MapView mapView)
	{
		Point ptpolygon1=mapView.getProjection().toPixels(geoPolygon1, null);
		Point ptpolygon2=mapView.getProjection().toPixels(geoPolygon2, null);
		Point ptpolygon3=mapView.getProjection().toPixels(geoPolygon3, null);
		Point ptpolygon4=mapView.getProjection().toPixels(geoPolygon4, null);
		
		Path pathpolygon=new Path();
		pathpolygon.moveTo(ptpolygon1.x, ptpolygon1.y);
		pathpolygon.lineTo(ptpolygon2.x, ptpolygon2.y);
		pathpolygon.lineTo(ptpolygon3.x, ptpolygon3.y);
		pathpolygon.lineTo(ptpolygon4.x, ptpolygon4.y);
		
		Paint paintpolygon=new Paint();
		paintpolygon.setStyle(Style.FILL);
		paintpolygon.setStrokeWidth(10);
		paintpolygon.setColor(Color.BLUE);
		paintpolygon.setAntiAlias(true);
		paintpolygon.setStrokeCap(Cap.ROUND);
		paintpolygon.setStrokeJoin(Join.ROUND);
		
		canvas.drawPath(pathpolygon, paintpolygon);
	}
}

class SimulateLocationOverlay extends Overlay {
	
	GeoPoint geoPoint;
	Bitmap bmpMarker;
	float fAccuracy=0f;
	

	public SimulateLocationOverlay(Bitmap mMarker) {
	    bmpMarker = mMarker;
	}
	
	public void setGeoCoords(GeoPoint geoSimulateLoc)
	{
		if(geoPoint==null)
		{
			geoPoint=new GeoPoint(geoSimulateLoc.getLatitudeE6(),geoSimulateLoc.getLongitudeE6());
		}
		else
		{
			geoPoint.setLatitudeE6(geoSimulateLoc.getLatitudeE6());
			geoPoint.setLongitudeE6(geoSimulateLoc.getLongitudeE6());
		}
	}
	
	public void setAccuracy(float fAccur)
	{
		fAccuracy=fAccur;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView) {
		if(geoPoint==null)
		{
			return;
		}
		Projection mapProjection = mapView.getProjection();
		Paint paint = new Paint();
		Point ptMap = mapProjection.toPixels(geoPoint, null);
		paint.setColor(Color.BLUE);
		paint.setAlpha(8);
		paint.setAntiAlias(true);

		float fRadius=mapProjection.metersToEquatorPixels(fAccuracy);
		canvas.drawCircle(ptMap.x, ptMap.y, fRadius, paint);
		paint.setStyle(Style.STROKE);
		paint.setAlpha(200);
		canvas.drawCircle(ptMap.x, ptMap.y, fRadius, paint);

		if(bmpMarker!=null)
		{
			paint.setAlpha(255);
			canvas.drawBitmap(bmpMarker, ptMap.x - bmpMarker.getWidth() / 2, ptMap.y
					- bmpMarker.getHeight() / 2, paint);
		}
		
		super.draw(canvas, mapView);
	}
}

