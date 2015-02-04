//
//  UserCheckinsViewController.h
//  Clubbook
//
//  Created by Andrew on 10/23/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseViewController.h"
#import "HeaderView.h"
#import "ClubFooterView.h"
#import "LocationManagerProtocol.h"
#import "ProfileCell.h"

@interface UserNearbyViewController : BaseViewController<LocationManagerProtocol, UICollectionViewDataSource, UICollectionViewDelegate, UITableViewDataSource, UITableViewDelegate, PNDelegate>
@property (weak, nonatomic) IBOutlet UICollectionView *profileCollection;
@property (nonatomic, strong) HeaderView *headerView;
@property (nonatomic, strong) ClubFooterView *clubFooterView;
@property (weak, nonatomic) IBOutlet UISlider *sliderControl;
@property (weak, nonatomic) IBOutlet UILabel *distance;
@property (weak, nonatomic) IBOutlet UISegmentedControl *usersSegment;
@property (weak, nonatomic) IBOutlet UITableView *filterMenuTable;

- (IBAction)sliderChanged:(id)sender;
- (IBAction)sliderTouchUp:(id)sender;

@end
