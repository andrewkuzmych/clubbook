//
//  YesterdayPlacesViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/28/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "BaseViewController.h"

@interface YesterdayPlacesViewController : BaseViewController <UITableViewDelegate, UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UITableView *placesTable;
@property (strong, nonatomic) NSArray *places;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *activityIndicator;
@property (weak, nonatomic) IBOutlet UILabel *noResultsLabel;

@end
