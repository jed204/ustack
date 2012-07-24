package com.untzuntz.ustack.uisupport;

import java.net.URL;

import nextapp.echo.app.Alignment;
import nextapp.echo.app.Border;
import nextapp.echo.app.Color;
import nextapp.echo.app.Extent;
import nextapp.echo.app.FillImage;
import nextapp.echo.app.FillImageBorder;
import nextapp.echo.app.Font;
import nextapp.echo.app.Insets;
import nextapp.echo.app.MutableStyle;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.Style;
import nextapp.echo.app.layout.ColumnLayoutData;
import nextapp.echo.app.layout.GridLayoutData;
import nextapp.echo.app.layout.RowLayoutData;
import nextapp.echo.webcontainer.Connection;

/**
 * Some Static Values
 * 
 * @author jdanner
 *
 */
public class UStackStatics {

	public static final Extent 		EX_0 		= new Extent(0);
	public static final Extent 		EX_1 		= new Extent(1);
	public static final Extent 		EX_2 		= new Extent(2);
	public static final Extent 		EX_5 		= new Extent(5);
	public static final Extent 		EX_10 		= new Extent(10);
	public static final Extent 		EX_11 		= new Extent(11);
	public static final Extent 		EX_12		= new Extent(12);
	public static final Extent 		EX_14		= new Extent(14);
	public static final Extent 		EX_15 		= new Extent(15);
	public static final Extent 		EX_20 		= new Extent(20);
	public static final Extent 		EX_25 		= new Extent(25);
	public static final Extent 		EX_30 		= new Extent(30);
	public static final Extent 		EX_33 		= new Extent(33);
	public static final Extent 		EX_50 		= new Extent(50);
	public static final Extent 		EX_55 		= new Extent(55);
	public static final Extent 		EX_60 		= new Extent(60);
	public static final Extent 		EX_75 		= new Extent(75);
	public static final Extent 		EX_100 		= new Extent(100);
	public static final Extent 		EX_110 		= new Extent(110);
	public static final Extent 		EX_125 		= new Extent(125);
	public static final Extent 		EX_150 		= new Extent(150);
	public static final Extent 		EX_200 		= new Extent(200);
	public static final Extent 		EX_215 		= new Extent(215);
	public static final Extent 		EX_225 		= new Extent(225);
	public static final Extent 		EX_230 		= new Extent(230);
	public static final Extent 		EX_250 		= new Extent(250);
	public static final Extent 		EX_280 		= new Extent(280);
	public static final Extent 		EX_300 		= new Extent(300);
	public static final Extent 		EX_315 		= new Extent(315);
	public static final Extent 		EX_325 		= new Extent(325);
	public static final Extent 		EX_375 		= new Extent(375);
	public static final Extent 		EX_395 		= new Extent(395);
	public static final Extent 		EX_400 		= new Extent(400);
	public static final Extent 		EX_450 		= new Extent(450);
	public static final Extent 		EX_500 		= new Extent(500);
	public static final Extent 		EX_550 		= new Extent(550);
	public static final Extent 		EX_650 		= new Extent(650);
	public static final Extent 		EX_750 		= new Extent(750);
	public static final Extent 		EX_800 		= new Extent(800);
	public static final Extent 		EX_900 		= new Extent(900);
	public static final Extent 		EX_1000 	= new Extent(1000);

	public static final Extent 		EX_25P	 	= new Extent(25, Extent.PERCENT);
	public static final Extent 		EX_33P	 	= new Extent(33, Extent.PERCENT);
	public static final Extent 		EX_50P	 	= new Extent(50, Extent.PERCENT);
	public static final Extent 		EX_99P	 	= new Extent(95, Extent.PERCENT);
	public static final Extent 		EX_100P 	= new Extent(100, Extent.PERCENT);

	public static final Insets		IN_0		= new Insets(0);
	public static final Insets		IN_1		= new Insets(1);
	public static final Insets		IN_2		= new Insets(2);
	public static final Insets		IN_3		= new Insets(3);
	public static final Insets		IN_5		= new Insets(5);
	public static final Insets		IN_10		= new Insets(10);
	public static final Insets		IN_15		= new Insets(15);
	public static final Insets		IN_25		= new Insets(25);
	public static final Insets		IN_28		= new Insets(28);
	public static final Insets		IN_BLOCK	= new Insets(5, 12, 5, 12);
	public static final Insets		IN_HSPACE	= new Insets(5, 0, 5, 0);
	public static final Insets		IN_WIDE		= new Insets(5, 2, 5, 2);
	public static final Insets		IN_VWIDE	= new Insets(9, 2, 9, 2);
	public static final Insets		IN_XWIDE	= new Insets(18, 2, 18, 0);

	public static final Insets		IN_T7		= new Insets(5, 7, 5, 5);
	
	
	public static final Insets		IN_TB_10	= new Insets(0, 10, 0, 10);
	
	public static final Color		VDARK_GRAY		= new Color(0x333344);
	public static final Color		DARK_BLUE		= new Color(0x294172);
	public static final Color		LIGHT_BLUE		= new Color(0x6699ff);
	public static final Color		DARK_GRAY		= new Color(0x555555);
	public static final Color		LIGHT_GRAY		= new Color(0xeeeeee);
	public static final Color		VLIGHT_GRAY		= new Color(0xf5f5f5);
	public static final Color		LIGHT_YELLOW	= new Color(0xffff66);
	public static final Color		VLIGHT_YELLOW	= new Color(0xffffbb);
	public static final Color		MED_BLUE		= new Color(0x284775);
	public static final Color		IND_BLUE		= new Color(0x607e98);
	public static final Color 		BLUE 			= new Color(0x8394a9);
	public static final Color		LBLUE 			= new Color(0x649de7);

	public static final Border		BDR_WHITE			= new Border(1, Color.WHITE, Border.STYLE_SOLID);
	public static final Border		BDR_SIMPLE			= new Border(1, DARK_GRAY, Border.STYLE_SOLID);
	public static final Border		BDR_SIMPLELIGHT		= new Border(1, LIGHT_GRAY, Border.STYLE_SOLID);
	public static final Border		BDR_BUTTON			= new Border(1, MED_BLUE, Border.STYLE_SOLID);
	public static final Border		BDR_BUTTON_OVER		= new Border(1, LIGHT_BLUE, Border.STYLE_SOLID);
	
	public static final Font		FONT_LARGE_SKINNY	= new Font(Font.ARIAL, Font.PLAIN, EX_25);
	public static final Font		FONT_LARGE			= new Font(Font.HELVETICA, Font.PLAIN, EX_20);
	public static final Font		FONT_LARGE_UL		= new Font(Font.HELVETICA, Font.UNDERLINE, EX_20);
	public static final Font		FONT_MED			= new Font(Font.HELVETICA, Font.PLAIN, EX_15);
	public static final Font		FONT_MED_UL			= new Font(Font.HELVETICA, Font.UNDERLINE, EX_15);
	public static final Font		FONT_MID_BOLD		= new Font(Font.HELVETICA, Font.BOLD, EX_14);
	public static final Font		FONT_MID			= new Font(Font.HELVETICA, Font.PLAIN, EX_14);
	public static final Font		FONT_MID_UL			= new Font(Font.HELVETICA, Font.UNDERLINE, EX_14);
	public static final Font		FONT_NORMAL			= new Font(Font.HELVETICA, Font.PLAIN, EX_12);
	public static final Font		FONT_NORMAL_BOLD	= new Font(Font.HELVETICA, Font.BOLD, EX_12);
	public static final Font		FONT_NORMAL_UL		= new Font(Font.HELVETICA, Font.UNDERLINE, EX_12);
	public static final Font		FONT_NORMAL_ITALICS	= new Font(Font.HELVETICA, Font.ITALIC, EX_12);
	public static final Font		FONT_SMALL			= new Font(Font.HELVETICA, Font.PLAIN, EX_10);
	public static final Font		FONT_SMALL_UL		= new Font(Font.HELVETICA, Font.UNDERLINE, EX_10);
	public static final Font		FONT_BOLD_XLARGE	= new Font(Font.HELVETICA, Font.BOLD, EX_75);
	public static final Font		FONT_BOLD_MLARGE	= new Font(Font.HELVETICA, Font.BOLD, EX_50);
	public static final Font		FONT_BOLD_LARGE		= new Font(Font.HELVETICA, Font.BOLD, EX_20);
	public static final Font		FONT_BOLD_MED		= new Font(Font.HELVETICA, Font.BOLD, EX_15);
	public static final Font		FONT_BOLD_SMALL		= new Font(Font.HELVETICA, Font.BOLD, EX_10);
	public static final Font		FONT_BOLD_UL_SMALL	= new Font(Font.HELVETICA, Font.BOLD | Font.UNDERLINE, EX_10);
	public static final Font		FONT_ERROR			= new Font(Font.HELVETICA, Font.BOLD, EX_12);
	public static final Font		FONT_COURIER		= new Font(Font.COURIER_NEW, Font.PLAIN, EX_12);
	public static final Font		FONT_ARIAL_11_BOLD	= new Font(Font.ARIAL, Font.BOLD, EX_11);
	public static final Font		FONT_ARIAL_11		= new Font(Font.ARIAL, Font.PLAIN, EX_11);
	public static final Font		FONT_ARIAL_12		= new Font(Font.ARIAL, Font.PLAIN, EX_12);

	
	public static final Style		GO_BUTTON			= getButtonStyle("GoButton");
	public static final Style		CANCEL_BUTTON		= getButtonStyle("CancelButton");
	public static final Style		LIGHT_BUTTON		= getButtonStyle("LightButton");
	public static final Style		ORANGE_BUTTON		= getButtonStyle("OrangeButton");
	public static final Style		WEB_BUTTON			= getButtonStyle("WebButton");
	public static final Style		MED_WEB_BUTTON		= getButtonStyle("MedWebButton");
	public static final Style		BIGLINK_BUTTON		= getButtonStyle("BigLinkButton");
	public static final Style		BOLD_BUTTON			= getButtonStyle("BoldButton");
	public static final Style		BOX_BUTTON			= getButtonStyle("BoxButton");
	public static final Style		BIGLINK_BL_BUTTON	= getButtonStyle("BigLinkBlackButton");
	public static final Style		COLOR_WEB_LINK		= getButtonStyle("ColorWebLink");
	public static final Style		FORWARD_BUTTON		= getButtonStyle("ForwardButton");
	public static final Style		BACK_BUTTON			= getButtonStyle("BackButton");
	public static final Style		BIG_BOY				= getButtonStyle("BigBoy");

	
	
	public static final GridLayoutData		GRID_SPAN2_RIGHT	= new GridLayoutData(2, 1);
	public static final GridLayoutData		GRID_SPAN2			= new GridLayoutData(2, 1);
	public static final GridLayoutData		GRID_RIGHT			= new GridLayoutData();
	public static final GridLayoutData		GRID_TOP			= new GridLayoutData();
	public static final GridLayoutData		GRID_CENTER			= new GridLayoutData();
	public static final ColumnLayoutData 	COLUMN_TOP			= new ColumnLayoutData();
	public static final ColumnLayoutData 	COLUMN_CENTER		= new ColumnLayoutData();
	public static final ColumnLayoutData 	COLUMN_BOTTOM		= new ColumnLayoutData();
	public static final ColumnLayoutData 	COLUMN_RIGHT		= new ColumnLayoutData();
	public static final RowLayoutData 		ROW_RIGHT			= new RowLayoutData();
	public static final RowLayoutData 		ROW_TOP				= new RowLayoutData();
	public static final RowLayoutData 		ROW_BOTTOM			= new RowLayoutData();
	static {
		GRID_SPAN2_RIGHT.setAlignment(Alignment.ALIGN_RIGHT);
		GRID_RIGHT.setAlignment(Alignment.ALIGN_RIGHT);
		GRID_CENTER.setAlignment(Alignment.ALIGN_CENTER);
		GRID_TOP.setAlignment(Alignment.ALIGN_TOP);
		COLUMN_CENTER.setAlignment(Alignment.ALIGN_CENTER);
		COLUMN_TOP.setAlignment(Alignment.ALIGN_TOP);
		COLUMN_BOTTOM.setAlignment(Alignment.ALIGN_BOTTOM);
		COLUMN_RIGHT.setAlignment(Alignment.ALIGN_RIGHT);
		ROW_RIGHT.setAlignment(Alignment.ALIGN_RIGHT);
		ROW_TOP.setAlignment(Alignment.ALIGN_TOP);
		ROW_BOTTOM.setAlignment(Alignment.ALIGN_BOTTOM);
	}
	
	public static final ResourceImageReference IMAGE_CDROM_BURN_16 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/cdburn-16.gif");
	public static final ResourceImageReference IMAGE_CDROM_64 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/cdrom-64.png");

	
	public static final ResourceImageReference IMAGE_BUILDING= new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/building-48.gif");
	public static final ResourceImageReference IMAGE_HOSPITAL = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/hospital-48.gif");
	public static final ResourceImageReference IMAGE_PEOPLE = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/people-48.gif");
	public static final ResourceImageReference IMAGE_BOSS = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/boss-48.gif");
	public static final ResourceImageReference IMAGE_USER = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/user-48.gif");
	public static final ResourceImageReference IMAGE_USER_CHECK = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/user-check-48.gif");
	public static final ResourceImageReference IMAGE_USER_GROUP = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/user-group-48.gif");
	public static final ResourceImageReference IMAGE_USER_INFO = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/user-info-48.gif");

	public static final ResourceImageReference IMAGE_GREENCHECK_16 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/greencheck-16.gif");
	public static final ResourceImageReference IMAGE_REDX_16 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/redx-16.gif");
	
	public static final ResourceImageReference IMAGE_LOCATION_16 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/location-16.gif");
	public static final ResourceImageReference IMAGE_FILE_IMPORT_16 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/file-import-16.gif");
	public static final ResourceImageReference IMAGE_FILE_EXPORT_16 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/file-export-16.gif");
	public static final ResourceImageReference IMAGE_EMAILSEND_16 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/email-send-16.gif");
	public static final ResourceImageReference IMAGE_SEARCH_16 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/search-16.gif");
	public static final ResourceImageReference IMAGE_ADD_16 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/add-16.gif");
	public static final ResourceImageReference IMAGE_CANCEL_16 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/cancel-16.gif");
	public static final ResourceImageReference IMAGE_REFRESH_16 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/refresh-16.gif");
	public static final ResourceImageReference IMAGE_ADD_24 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/add-24.gif");
	public static final ResourceImageReference IMAGE_CANCEL_24 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/cancel-24.gif");
	public static final ResourceImageReference IMAGE_REFRESH_24 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/refresh-24.gif");
	public static final ResourceImageReference IMAGE_PHONE = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/phone.gif");
	public static final ResourceImageReference IMAGE_GLOBE_48 = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/globe-48.gif");

	public static final ResourceImageReference IMAGE_BTN_BG1= new ResourceImageReference("com/untzuntz/ustack/resources/images/btn/btn1.gif");

	public static final ResourceImageReference IMAGE_SLIM = new ResourceImageReference("com/untzuntz/ustack/resources/images/slim.png");
	public static final ResourceImageReference IMAGE_SLIM_UP = new ResourceImageReference("com/untzuntz/ustack/resources/images/slimup.png");
	public static final ResourceImageReference IMAGE_GRAY2WHITE = new ResourceImageReference("com/untzuntz/ustack/resources/images/gray2white.png");
	public static final ResourceImageReference IMAGE_GRAY2LGRAY = new ResourceImageReference("com/untzuntz/ustack/resources/images/gray2lgray.png");
	public static final ResourceImageReference IMAGE_BLUEGRAD = new ResourceImageReference("com/untzuntz/ustack/resources/images/bluegrad.png");
	public static final ResourceImageReference IMAGE_BLUEBUTTON = new ResourceImageReference("com/untzuntz/ustack/resources/images/bluebutton.png");
	public static final ResourceImageReference IMAGE_GRAYBUTTON = new ResourceImageReference("com/untzuntz/ustack/resources/images/graybutton.png");
	public static final ResourceImageReference IMAGE_BLUEBUTTON_25 = new ResourceImageReference("com/untzuntz/ustack/resources/images/bluebutton-25.png");
	public static final ResourceImageReference IMAGE_GRAYBUTTON_25 = new ResourceImageReference("com/untzuntz/ustack/resources/images/graybutton-25.png");
	public static final ResourceImageReference IMAGE_BLUEBUTTON_LARGE = new ResourceImageReference("com/untzuntz/ustack/resources/images/bluebutton-lrg.png");
	public static final ResourceImageReference IMAGE_GRAYBUTTON_LARGE = new ResourceImageReference("com/untzuntz/ustack/resources/images/graybutton-lrg.png");

	public static final ResourceImageReference IMAGE_CLOSE = new ResourceImageReference("com/untzuntz/ustack/resources/images/btn/close.png");
	public static final ResourceImageReference IMAGE_CLOSE_HIGHLIGHT = new ResourceImageReference("com/untzuntz/ustack/resources/images/btn/close-hi.png");
	public static final ResourceImageReference IMAGE_HELP = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/help-16.png");
	public static final ResourceImageReference IMAGE_HELP_BW = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/help-16-bw.png");
	public static final ResourceImageReference IMAGE_FEEDBACK = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/feedback-16.png");
	public static final ResourceImageReference IMAGE_FEEDBACK_BW = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/feedback-16-bw.png");
	public static final ResourceImageReference IMAGE_HOME = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/home-16.png");
	public static final ResourceImageReference IMAGE_HOME_BW = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/home-16-bw.png");

	public static final ResourceImageReference IMAGE_BG_CURVE1 = new ResourceImageReference("com/untzuntz/ustack/resources/images/bg-curve1.png");
	
	public static final ResourceImageReference IMAGE_USER_16_PNG = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/user-16.png");
	public static final ResourceImageReference IMAGE_BOSS_16_PNG = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/boss-16.png");
	public static final ResourceImageReference IMAGE_SEARCH_16_PNG = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/search-16.png");
	public static final ResourceImageReference IMAGE_LOCATION_16_PNG = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/location-16.png");
	public static final ResourceImageReference IMAGE_OUT_16_PNG = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/out-16.png");
	public static final ResourceImageReference IMAGE_IN_16_PNG = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/in-16.png");
	public static final ResourceImageReference IMAGE_ADD_16_PNG = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/user-16.png");
	
	public static final ResourceImageReference IMAGE_USER_16_PNG_BW = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/user-16-bw.png");
	public static final ResourceImageReference IMAGE_BOSS_16_PNG_BW = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/boss-16-bw.png");
	public static final ResourceImageReference IMAGE_SEARCH_16_PNG_BW = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/search-16-bw.png");
	public static final ResourceImageReference IMAGE_LOCATION_16_PNG_BW = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/location-16-bw.png");
	public static final ResourceImageReference IMAGE_OUT_16_PNG_BW = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/out-16-bw.png");
	public static final ResourceImageReference IMAGE_IN_16_PNG_BW = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/in-16-bw.png");
	public static final ResourceImageReference IMAGE_ADD_16_PNG_BW = new ResourceImageReference("com/untzuntz/ustack/resources/images/ico/user-16-bw.png");

	
	public static Style getButtonStyle(String name)
	{
		MutableStyle ret = new MutableStyle();
		
		if ("GoButton".equalsIgnoreCase(name))
		{
			ret.set("background", DARK_BLUE);
			ret.set("foreground", Color.WHITE);
			ret.set("insets", IN_WIDE);
			ret.set("border", BDR_SIMPLE);
			ret.set("font", FONT_BOLD_SMALL);
		}
		else if ("BoxButton".equalsIgnoreCase(name))
		{
			ret.set("background", Color.WHITE);
			ret.set("foreground", Color.BLACK);
			ret.set("insets", IN_BLOCK);
			ret.set("font", FONT_NORMAL);
			ret.set("rolloverEnabled", Boolean.TRUE);
			ret.set("rolloverBackground", Color.DARKGRAY);
			ret.set("rolloverForeground", Color.WHITE);
		}
		else if ("CancelButton".equalsIgnoreCase(name))
		{
			ret.set("background", Color.LIGHTGRAY);
			ret.set("foreground", Color.WHITE);
			ret.set("insets", IN_WIDE);
			ret.set("border", BDR_SIMPLE);
			ret.set("font", FONT_NORMAL_BOLD);
		}
		else if ("BigBoy".equalsIgnoreCase(name))
		{
			ret.set("insets", IN_3);
			ret.set("font", FONT_MED);
		}
		else if ("LightButton".equalsIgnoreCase(name))
		{
			ret.set("foreground", Color.LIGHTGRAY);
			ret.set("font", FONT_SMALL);
		}
		else if ("BackButton".equalsIgnoreCase(name))
		{
			ret.set("foreground", Color.BLACK);
			ret.set("border", BDR_SIMPLELIGHT);
			ret.set("background", LIGHT_GRAY);
			ret.set("rolloverEnabled", true);
			ret.set("rolloverBorder", BDR_SIMPLE);
			ret.set("height", new Extent(16));
			ret.set("insets", new Insets(15, 2, 15, 0));
		}
		else if ("ForwardButton".equalsIgnoreCase(name))
		{
			ret.set("foreground", Color.WHITE);
			ret.set("border", BDR_BUTTON);
			ret.set("background", IND_BLUE);
			ret.set("rolloverEnabled", true);
			ret.set("rolloverBorder", BDR_BUTTON_OVER);
			ret.set("height", new Extent(16));
			ret.set("insets", new Insets(15, 2, 15, 0));
		}
		else if ("ColorWebLink".equalsIgnoreCase(name))
		{
			ret.set("font", FONT_MED_UL);
			ret.set("foreground", DARK_BLUE);
		}
		else if ("MedWebButton".equalsIgnoreCase(name))
		{
			ret.set("font", FONT_MED_UL);
		}
		else if ("WebButton".equalsIgnoreCase(name))
		{
			ret.set("font", FONT_NORMAL_UL);
		}
		else if ("BigLinkButton".equalsIgnoreCase(name))
		{
			ret.set("foreground", Color.WHITE);
			ret.set("font", FONT_BOLD_LARGE);
		}
		else if ("BigLinkBlackButton".equalsIgnoreCase(name))
		{
			ret.set("font", FONT_LARGE_UL);
		}
		else if ("BoldButton".equalsIgnoreCase(name))
		{
			ret.set("font", FONT_BOLD_MED);
		}
		
		return ret;
	}
	
	
    public static final FillImageBorder FIB1_SURROUND = 
        new FillImageBorder(null, new Insets(17, 17, 23, 23), new Insets(8, 8, 14, 14), 
        new FillImage[] {
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderTopLeft.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderTop.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderTopRight.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderLeft.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderRight.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderBottomLeft.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderBottom.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderBottomRight.png"))
        });
	
    public static final Insets FIB1_TOP_INSETS = new Insets(8, 8, 14, 0);
    public static final FillImageBorder FIB1_TOP = 
        new FillImageBorder(null, new Insets(17, 17, 23, 0), FIB1_TOP_INSETS, 
        new FillImage[] {
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderTopLeft.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderTop.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderTopRight.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderLeft.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderRight.png")),
            null,
            null,
            null
        });

	public static final FillImageBorder FIB1_BOTTOM = 
        new FillImageBorder(null, new Insets(17, 0, 23, 23), new Insets(8, 0, 14, 14), 
        new FillImage[] {
            null,
            null,
            null,
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderLeft.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderRight.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderBottomLeft.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderBottom.png")),
            new FillImage(new ResourceImageReference("com/untzuntz/ustack/resources/images/BorderBottomRight.png"))
        });

	
	public static String getBaseURL()
	{
		URL myURL = null;
		
		try {
			Connection conn = nextapp.echo.webcontainer.WebContainerServlet.getActiveConnection();
	    	myURL = new URL( conn.getRequest().getRequestURL().toString() );
		} catch (Exception err) { 
			return null;
		}
	
		String port = "";
		String file = myURL.getFile();
	
		if (myURL != null)
		{
			if (myURL.getPort() > -1)
				port = ":" + myURL.getPort();
			if (file.indexOf("/ref/") > -1)
				file = file.substring(0, file.indexOf("/ref/"));
		}
	
		return myURL.getProtocol() + "://" + myURL.getHost() + port;
	}
	


}
