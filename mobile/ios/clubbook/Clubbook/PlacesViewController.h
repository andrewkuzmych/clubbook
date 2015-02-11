//
//  Created by Clubbok.
//

#import <UIKit/UIKit.h>

#import "BaseViewController.h"
#import "SPSlideTabView.h"
#import "ClubsInfiniteTableView.h"

@interface PlacesViewController : BaseViewController<PNDelegate, UISearchBarDelegate>

@property (weak, nonatomic) IBOutlet SPSlideTabView *slideTabBarView;
@property (weak, nonatomic) IBOutlet UISearchBar *searchBar;

@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *activityIndicator;
@property (weak, nonatomic) IBOutlet UILabel *noResultsLabel;

@property (strong, nonatomic) ClubsInfiniteTableView* clubTable;
@property (strong, nonatomic) ClubsInfiniteTableView* barsTable;
@property (strong, nonatomic) ClubsInfiniteTableView* djTable;
@property (strong, nonatomic) ClubsInfiniteTableView* eventsTable;

@end
