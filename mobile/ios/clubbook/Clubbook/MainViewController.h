//
//  ViewController.h
//  SidebarDemo
//
//  Created by Simon on 28/6/13.
//  Copyright (c) 2013 Appcoda. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>
#import "BaseViewController.h"
#import "LocationManagerProtocol.h"
#import "SPSlideTabBar.h"

@interface MainViewController : BaseViewController<LocationManagerProtocol,UITableViewDelegate, UITableViewDataSource, PNDelegate, SPSlideTabBarDelegate>{
 CLLocationManager *locationManager;
}
@property (weak, nonatomic) IBOutlet UITableView *clubTable;
@property (weak, nonatomic) IBOutlet UISlider *sliderControl;
@property (weak, nonatomic) IBOutlet UILabel *distance;
@property (weak, nonatomic) IBOutlet UISegmentedControl *segmentControl;
@property (strong, nonatomic) NSMutableArray *places;
@property (weak, nonatomic) IBOutlet UIScrollView *filterTabView;
@property (strong, nonatomic) IBOutlet SPSlideTabBar *filterTabBar;
- (IBAction)SliderChanged:(id)sender;
- (IBAction)sliderTouchUp:(id)sender;
//- (IBAction)checkinAction:(UIButton *)sender;
- (IBAction)segmentChanged:(id)sender;

@end
