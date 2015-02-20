//
//  NewsView.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/19/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "NewsView.h"

@implementation NewsView

- (id) init {
    NSArray* subviewArray = [[NSBundle mainBundle] loadNibNamed:@"NewsView" owner:self options:nil];
    id mainView = [subviewArray objectAtIndex:0];
    
    return mainView;
}

- (id) initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
    }
    return self;
}

- (id) initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
    }
    return self;
}

- (void) awakeFromNib {
}

- (void) customInitType:(double) userLat userLon:(double)userLon accessToken:(NSString*) accessToken type:(NSString*) type objectId:(NSString*) objectId andParentViewCntroller:(UIViewController*) parent {
    self.newsTable.infiniteDelegate = self;
    [self.newsTable setSeparatorStyle:UITableViewCellSeparatorStyleNone];
    [self.newsTable initializeNewsTableType:userLat userLon:userLon accessToken:accessToken type:type objectId:objectId andParentViewCntroller:parent];
}

- (void)tableIsLoading {
    [self.activityIndicator setHidden:NO];
    [self.noDataLabel setHidden:YES];
}

- (void) tableIsEmpty {
    [self.newsTable setHidden:YES];
    [self.activityIndicator setHidden:YES];
    [self.noDataLabel setHidden:NO];
}

- (void) tableNotEmpty {
    [self.newsTable setHidden:NO];
    [self.activityIndicator setHidden:YES];
    [self.noDataLabel setHidden:YES];
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
