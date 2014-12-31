//
//  Created by Clubbok.
//

#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>

#import "BaseViewController.h"
#import "SPSlideTabBar.h"

@interface MainViewController : BaseViewController<UITableViewDelegate, UITableViewDataSource, PNDelegate, SPSlideTabBarDelegate, UISearchBarDelegate, UIScrollViewDelegate>{
 CLLocationManager *locationManager;
}
@property (weak, nonatomic) IBOutlet UITableView *clubTable;
@property (weak, nonatomic) IBOutlet UISlider *sliderControl;
@property (strong, nonatomic) NSMutableArray *places;

@property (weak, nonatomic) IBOutlet UIScrollView *filterTabView;
@property (strong, nonatomic) IBOutlet SPSlideTabBar *filterTabBar;
@property (weak, nonatomic) IBOutlet UISearchBar *searchBar;

@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *activityIndicator;
@property (weak, nonatomic) IBOutlet UILabel *noResultsLabel;

@property (nonatomic) BOOL showAllPlaces;

@end
