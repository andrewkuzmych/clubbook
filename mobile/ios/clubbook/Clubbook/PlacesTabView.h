//
//  PlacesTabView.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/11/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "InfiniteScrollTableView.h"

@interface PlacesTabView : UIView <InfiniteScrollTableViewDelegate>

@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *activityIndicator;
@property (weak, nonatomic) IBOutlet UILabel *noDataLabel;

@property (strong, nonatomic) InfiniteScrollTableView* table;

- (void) addTableToTheView:(InfiniteScrollTableView*)iTable;

- (void) setNoDataLabelText:(NSString*)text;

@end
