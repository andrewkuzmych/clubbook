//
//  FastCheckinViewController.h
//  Clubbook
//
//  Created by Andrew on 10/25/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseViewController.h"
#import "LocationManagerProtocol.h"

@interface FastCheckinViewController : BaseViewController<LocationManagerProtocol>
@property (weak, nonatomic) IBOutlet UILabel *noCheckinsLabel;
@property (weak, nonatomic) IBOutlet UITableView *clubTable;
@property (strong, nonatomic) NSMutableArray *places;
- (IBAction)checkinAction:(id)sender;
@end
