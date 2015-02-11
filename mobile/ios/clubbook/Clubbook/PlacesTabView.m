//
//  PlacesTabView.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/11/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "PlacesTabView.h"

@implementation PlacesTabView

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (id) init {
    NSArray* subviewArray = [[NSBundle mainBundle] loadNibNamed:@"PlaceTabView" owner:self options:nil];
    id mainView = [subviewArray objectAtIndex:0];
    
    return mainView;
}


- (id) initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        
        
    }
    return self;
}

- (void) addTableToTheView:(InfiniteScrollTableView*)iTable {
    if (self.table != nil) {
        [self.table removeFromSuperview];
        self.table = nil;
    }
    
    self.table = iTable;
    self.table.infiniteDelegate = self;

    [self addSubview:self.table];
    self.table.translatesAutoresizingMaskIntoConstraints = NO;
    
    UIView* subview = self.table;
    
    NSDictionary *views = NSDictionaryOfVariableBindings(subview);
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|[subview]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:views]];
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[subview]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:views]];
    
    [self.table makeInitialLoad];
}

- (void) setNoDataLabelText:(NSString *)text {
    [self.noDataLabel setText:text];
}

- (void)tableIsLoading {
    [self.activityIndicator setHidden:NO];
    [self.noDataLabel setHidden:YES];
}

- (void) tableIsEmpty {
    [self.activityIndicator setHidden:YES];
    [self.noDataLabel setHidden:NO];
}

- (void) tableNotEmpty {
    [self.activityIndicator setHidden:YES];
    [self.noDataLabel setHidden:YES];
}

@end
