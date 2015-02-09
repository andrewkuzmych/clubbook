//
//  FloatingButton.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/6/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "FloatingButton.h"

@implementation FloatingButton

+ (id)buttonWithFrame:(CGRect)frame {
    return [[self alloc] initWithFrame:frame];
}

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self initButton];
    }
    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self initButton];
    }
    return self;
}

- (void) initButton {
    [self changeStatus:NO];
    self.layer.masksToBounds = NO;
    self.titleLabel.textAlignment = NSTextAlignmentCenter;
    self.layer.cornerRadius = 40;
    self.layer.shadowColor = [UIColor colorWithWhite:0.000 alpha:0.710].CGColor;
    self.layer.shadowOpacity = 0.5;
    self.titleEdgeInsets = UIEdgeInsetsMake(0, 0, 0, 0);
    self.layer.shadowRadius = 7;
    self.layer.shadowOffset = CGSizeMake(7.0f, 7.0f);
    
    self.onTitle = @"On";
    self.offTitle = @"Off";
}

- (void) changeStatus:(BOOL) isOn {
    self.statusOn = isOn;
    dispatch_queue_t q = dispatch_get_main_queue();
    if (self.statusOn) {
        
        dispatch_async(q, ^{
           [self setBackgroundColor:[UIColor colorWithRed:0.698 green:0.000 blue:0.000 alpha:1.000]];
           [self.titleLabel setText:self.offTitle];
        });
    }
    else {
        
        dispatch_async(q, ^{
           [self setBackgroundColor:[UIColor colorWithRed:0.000 green:0.643 blue:0.000 alpha:1.000]];
           [self.titleLabel setText:self.onTitle];
        });
    }
}

- (void) setOnTitle:(NSString *)onTitle andOffTitle:(NSString*)offTitle {
    self.onTitle = onTitle;
    self.offTitle = offTitle;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
